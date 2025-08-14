package windowStuff;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.SourceDataLine;


public final class Audio {

  private static final HashMap<String, byte[]> sounds = new HashMap<>(5);
  private static final AudioFormat audioFormat = new AudioFormat(
      Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
  private static final Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
  private static final List<SoundPlayer> players = new ArrayList<>();
  private static final List<SoundToPlay> queued = new ArrayList<>();
  private static final byte[] EMPTY = new byte[0];
  private static final Lock inactiveLock = new ReentrantLock();
  private static boolean active = true;
  private static boolean deleteAllSounds = false;
  private Audio() {
  }

  public static boolean isActive() {
    return active;
  }

  public static void setActive(boolean active) {
    Audio.active = active;
    if (active) {
      inactiveLock.unlock();
    } else {
      deleteAllSounds = true;
      inactiveLock.lock();
    }
  }

  public static byte[] load(String name) {
    try {
      URL url = Audio.class.getResource("/sounds/" + name + ".wav");
      if(url==null){
        return null;
      }
      // The wav file named above was obtained from https://freesound.org/people/Robinhood76/sounds/371535/
      // and matches the audioFormat.
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
      int bytesRead = 0;
      byte[] buffer = audioInputStream.readAllBytes();
      audioInputStream.close();
      return buffer;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static byte[] getSound(String name){
    var found = sounds.get(name);
    if(found!=null){
      return found;
    }
    var generated = load(name);
    if(generated==null){
      return getSound("notFound");
    }
    sounds.put(name, generated);
    return generated;
  }

  public static void play(String name, float volume) {
    play(new SoundToPlay(name, volume));
  }

  public static void play(SoundToPlay sound) {
    if (!active) {
      return;
    }
    synchronized (queued) {
      queued.add(sound);
    }
  }

  public static void playNow(SoundToPlay sound) {
    SoundPlayer minPlayer = players.get(0);
    for (var player : players) {
      if (player.remaining() <= 0) {
        player.play(sound);
        return;
      }
      if (player.remaining() < minPlayer.remaining()) {
        minPlayer = player;
      }
    }
    minPlayer.play(sound);
  }

  public static void init() {
    new Thread(Audio::loop).start();
  }

  private static void loop() {
    for (int i = 0; i < 20; i++) {
      var p = new SoundPlayer();
      players.add(p);
    }
    while (Window.get().isRunning()) {
      if (deleteAllSounds) {
        for (var player : players) {
          player.flush();
        }
        deleteAllSounds = false;
      }
      inactiveLock.lock();
      inactiveLock.unlock();
      List<SoundToPlay> dequeued;
      synchronized (queued) {
        dequeued = new ArrayList<>(queued);
        queued.clear();
      }
      for (var sound : dequeued) {
        playNow(sound);
      }
      for (var player : players) {
        player.tick();
      }
    }
    for (var player : players) {
      player.delete();
    }
  }

  public static class SoundToPlay {

    public final String name;
    public final float volume;

    public SoundToPlay(String name, float volume) {
      this.name = name;
      this.volume = volume;
    }
  }

  private static class SoundPlayer {

    SourceDataLine sourceDataLine;
    byte[] buffer = EMPTY;
    int read = 0;
    FloatControl noiseCtrl;

    SoundPlayer() {
      try {
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        noiseCtrl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public int remaining() {
      return buffer.length - read;
    }

    void play(SoundToPlay sound) {
      final float VOLUME_RANGE = 35;
      noiseCtrl.setValue(Math.max(noiseCtrl.getMinimum(), noiseCtrl.getMaximum() - (1-sound.volume) * VOLUME_RANGE ));

      buffer = getSound(sound.name);
      read = 0;
      sourceDataLine.flush();
    }

    void flush() {
      read = 0;
      sourceDataLine.flush();
      buffer = EMPTY;
    }

    void tick() {
      if (remaining() <= 0) {
        return;
      }
      read += sourceDataLine.write(buffer, read,
          Math.min(sourceDataLine.available(), remaining()));
    }

    void delete() {
      sourceDataLine.drain();
      sourceDataLine.close();
    }
  }
}

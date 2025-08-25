package windowStuff;

import general.Log;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
      Encoding.PCM_SIGNED, 48000, 16, 2, 4, 48000, false);
  private static final Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
  private static final byte[] EMPTY = new byte[0];
  private static final Map<String, AudioGroup> groups;
  static{
    List<String> soundTypes = List.of("sfx", "music");
    List<Integer> soundChannels = List.of(25, 2);
    groups = new HashMap<>(soundTypes.size());
    for (int i = 0; i < soundTypes.size(); i++) {
      groups.put(soundTypes.get(i), new AudioGroup(soundChannels.get(i)));
    }
  }

  public static Set<String> getGroups(){
    return groups.keySet();
  }

  public static AudioGroup getGroup(String name){
    return groups.get(name);
  }

  private Audio() {
  }

  public static class AudioGroup{
    private final Lock inactiveLock = new ReentrantLock();
    private boolean active = true;
    private boolean deleteAllSounds = false;
    private final List<SoundPlayer> players = new ArrayList<>();
    private final List<SoundToPlay> queued = new ArrayList<>();
    private boolean updateVolume=false;
    private int channels;

    private AudioGroup(int channels){
      this.channels=channels;
    }

    public float getVolumeMultiplier() {
      return volumeMultiplier;
    }

    public void setVolumeMultiplier(float volumeMultiplier) {
      this.volumeMultiplier = volumeMultiplier;
      updateVolume=true;
    }

    private float volumeMultiplier = 1;

    public boolean isActive() {
      return active;
    }

    public void setActive(boolean active) {
      if(this.active==active){
        return;
      }
      this.active = active;
      if (active) {
        inactiveLock.unlock();
      } else {
        deleteAllSounds = true;
        inactiveLock.lock();
      }
    }

    public void clear(){
      deleteAllSounds = true;
    }

    public void play(SoundToPlay sound) {
      if (!active) {
        return;
      }
      synchronized (queued) {
        queued.add(sound);
      }
    }

    private void playNow(SoundToPlay sound) {
      SoundPlayer minPlayer = players.get(0);
      for (var player : players) {
        if (player.remaining() <= 0) {
          player.play(sound);
          return;
        }
        if (player.remaining() < minPlayer.remaining() && player.currentSound ==null) {
          minPlayer = player;
        }
      }
      minPlayer.play(sound);
    }

    private void loop() {
      for (int i = 0; i < channels; i++) {
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
        if (updateVolume) {
          for (var player : players) {
            player.setVolumeMultiplier(volumeMultiplier);
          }
          updateVolume = false;
        }
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

  public static void play(String name, float volume, String type) {
    play(new SoundToPlay(name, volume, type));
  }

  public static void play(String name, float volume) {
    play(new SoundToPlay(name, volume));
  }

  public static void play(SoundToPlay sound) {
    try{
      groups.get(sound.type).play(sound);
    }catch (NullPointerException e) {
      Log.write("no such sound type: "+sound.type);
    }
  }


  public static void init() {
    for(var g : groups.values()){
      new Thread(g::loop).start();
    }
  }

  public static class SoundToPlay {

    public final String name;
    public final float volume;
    public final String type;
    public final boolean loop;

    public SoundToPlay(String name, float volume) {
      this(name,volume,"sfx");
    }

    public SoundToPlay(String name, float volume, String type) {
      this(name,volume,type, false);
    }

    public SoundToPlay(String name, float volume, String type, boolean loop) {
      this.name = name;
      this.volume = volume;
      this.type=type;
      this.loop=loop;
    }
  }

  private static class SoundPlayer {

    SourceDataLine sourceDataLine;
    byte[] buffer = EMPTY;
    int read = 0;
    FloatControl noiseCtrl;
    private SoundToPlay currentSound = null;
    private float volumeMultiplier = 1;

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

    private void updateVolume(){
      final float VOLUME_RANGE = 50;
      if(currentSound==null){
        return;
      }
      noiseCtrl.setValue(Math.max(noiseCtrl.getMinimum(), noiseCtrl.getMaximum() - (1-currentSound.volume*volumeMultiplier) * VOLUME_RANGE));
    }

    void play(SoundToPlay sound) {
      currentSound = sound;
      updateVolume();

      buffer = getSound(sound.name);
      read = 0;
      sourceDataLine.flush();
    }

    void setVolumeMultiplier(float value){
      volumeMultiplier=value;
      updateVolume();
    }

    void flush() {
      read = 0;
      sourceDataLine.flush();
      buffer = EMPTY;
      currentSound =null;
    }

    void tick() {
      if (remaining() <= 0) {
        if(currentSound !=null && currentSound.loop){
          play(currentSound);
        }
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

package windowStuff;

import general.Log;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
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
  private static List<SoundThread> players = new ArrayList<>();

  private Audio() {
  }

  public static byte[] load(String name){
    try {
      URL url = Audio.class.getResource("/sounds/" + name + ".wav");
      // The wav file named above was obtained from https://freesound.org/people/Robinhood76/sounds/371535/
      // and matches the audioFormat.
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
      int bytesRead = 0;
      byte[] buffer = audioInputStream.readAllBytes();
      audioInputStream.close();
      return buffer;
    }catch (Exception e) {
      e.printStackTrace();
    }
    return new byte[0];
  }

  public static void play(String name, float volume){
    if(players.isEmpty()){
      init();
    }
    for(var player : players){
      if(player.open){
        player.play(name,volume);
        return;
      }
    }
  }

  private static void init(){
    for(int i=0;i<99;i++){
      var p = new SoundThread();
      players.add(p);
      Thread t = new Thread(()->{
        while(Window.get().isRunning()){p.tick();}
        p.delete();
      });
      t.start();
    }
  }

  public static void kill(){
    for(var player : players){
      player.delete();
    }
  }

  private static class SoundThread{
    SourceDataLine sourceDataLine;
    byte[] buffer = new byte[0];
    int read = 0;
    FloatControl noiseCtrl;
    boolean open = true;

    Lock openLock = new ReentrantLock();
    final Condition workCompleted = openLock.newCondition();

    SoundThread(){
      try {
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        noiseCtrl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
      }catch (Exception e) {
        e.printStackTrace();
      }
    }

    void play(String name, float volume){
      try {
        openLock.lock();
        noiseCtrl.setValue(
            noiseCtrl.getMinimum() + (noiseCtrl.getMaximum() - noiseCtrl.getMinimum()) * volume);
        synchronized (sounds) {
          buffer = sounds.computeIfAbsent(name, Audio::load);
        }
        read = 0;
        open = false;
        workCompleted.signalAll();
      }finally {
        openLock.unlock();
      }
    }

    void tick(){
      try {
        openLock.lock();
        if (open) {
          workCompleted.await();
        }

        read += sourceDataLine.write(buffer, read, buffer.length);
        sourceDataLine.drain();
        open = read >= buffer.length;
      }catch (Exception e) {
        e.printStackTrace();
      }finally{
        openLock.unlock();
      }
    }

    void delete(){
      try {
        openLock.lock();
      sourceDataLine.drain();
      sourceDataLine.close();
        workCompleted.signalAll();
      }finally {
        openLock.unlock();
      }
    }
  }
}

package windowStuff.GraphicsOnly.Text;

import GlobalUse.Log;
import GlobalUse.Util;

public class TextModifiers {//mb this should be an enum but I can't really think of a reason why it would work better that way?
  //the color is in RGB but like there's a problem because the shader doesn't really work the way one might think it does.
  //until I make a new shader after bitching about the current one max saturation can only be achieved if R+G+B==765(three times 255).
  //then again mixing color with sprite was always kinda cursed so I guess it's whatever...

  public static final String red = "|#765.#25.#25|";
  public static final String resetColor = "|#999.#999.#999|";//999 is a special value that resets the color to the original
  public static final String green = "|#25.#765.#25|";
  public static final String moneyYellow = "|#330.#335.#100|";
  public static final String livesRed = "|#615.#75.#75|";
  public static final String blue = "|#25.#25.#765|";
  public static final String white = "|#255.#255.#255|";
  public static final String Grey = "|#125.#125.#125|";
  public static final String titleSize = "|#70|";
  public static final String normalSize = "|#35|";
  public static final String smallSize = "|#25|";
  public static final String reset = "|#<|";
  public static final String gigaReset = "|#<<|";
  public static String size(int size){
    return "|#"+size+"|";
  }
  public static String RGBcolors(int[] colors){
    if(colors.length!=3){
      Log.write("wtf, color not of len 3? rgba is currently not really a thing for text mods");
      return "wtf, color not of len 3? rgba is currently not really a thing for text mods";
    }else{
      int totalColor=colors[0]+colors[1]+colors[2];
      float[] trueColor=new float[3];
      trueColor[0]=colors[0]*765f/totalColor;
      trueColor[1]=colors[1]*765f/totalColor;
      trueColor[2]=colors[2]*765f/totalColor;
      return textModColors(new float[]{colors[0],colors[1],colors[2]});
    }
  }
  public static String textModColors(float[] colors){
    StringBuilder str = new StringBuilder("|#");
    for(int i=0;i<colors.length;i++){
      if(i!=0){
        str.append(".#");
      }
      str.append((int)colors[i]);
    }
    str.append("|");
    return str.toString();
  }
  public static String colors(float[] colors){
    StringBuilder str = new StringBuilder("|color:");
    for(int i=0;i<colors.length;i++){
      if(i!=0){
        str.append(',');
      }
      str.append(colors[i]);
    }
    str.append("|");
    return str.toString();
  }
}

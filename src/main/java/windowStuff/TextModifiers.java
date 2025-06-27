package windowStuff;

public class TextModifiers {//mb this should be an enum but I can't really think of a reason why it would work better that way?
  //the color is in RGB but like there's a problem because the shader doesn't really work the way one might think it does.
  //until I make a new shader after bitching about the current one max saturation can only be achieved if R+G+B==765(three times 255).
  //then again mixing color with sprite was always kinda cursed so I guess it's whatever...

  public static final String red = "|#765.#25.#25|";
  public static final String green = "|#25.#765.#25|";
  public static final String blue = "|#25.#25.#765|";
  public static final String white = "|#255.#255.#255|";
  public static final String Grey = "|#125.#125.#125|";
  public static final String titleSize = "|#70|";
  public static final String normalSize = "|#35|";
  public static final String smallSize = "|#25|";
}

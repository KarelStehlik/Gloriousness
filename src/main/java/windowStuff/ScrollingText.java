package windowStuff;

import general.Constants;
import general.Log;
import general.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ScrollingText implements Text {

  private static final float textureHeight =
      128f / 4096f; //the height of a glyph sub-texture, in uv coordinates
  private int layer;
  private final String fontName;
  private final int maxWidth;
  private final SpriteBatching bs;
  private final AbstractSprite background;
  private int x, y;
  private final TextGenerator textupdater;
  private final ArrayList<Symbol> symbols = new ArrayList<>(0);
  private String text;
  private boolean hidden = false;
  private Style baseStyle, currentStyle;
  private final List<Style> styles = new ArrayList<>(0);
  private float scroll = 0;
  private int startChar = 0;

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  private float speed = 6;

  public ScrollingText(String value, int width, int layer, float size, SpriteBatching bs, String bg) {
    this(value, "Calibri", width, 0, 0, layer, size, bs, "basic", bg);
  }

  public ScrollingText(String value, int width, int layer, float size, SpriteBatching bs) {
    this(value, "Calibri", width, 0, 0, layer, size, bs, "basic", null);
  }

  public ScrollingText(String value, String font, int width, int x, int y, int layer, float size,
      SpriteBatching bs) {
    this(value, font, width, x, y, layer, size, bs, "basic", null);
  }

  public ScrollingText(TextGenerator value, String font, int width, int x, int y, int layer,
      float size,
      SpriteBatching bs) {
    this(value, font, width, x, y, layer, size, bs, "basic", null);
  }

  public ScrollingText(String value, String font, int width, int x, int y, int layer, float size,
      SpriteBatching bs, String shader, String backgroundImage) {
    this(new StaticTextGenerator(value), font, width, x, y, layer, size, bs, shader, backgroundImage);
  }

  public ScrollingText(TextGenerator value, String font, int width, int x, int y, int layer,
      float size,
      SpriteBatching bs, String shader, String backgroundImage) {

    this.textupdater = value;
    baseStyle = new Style(size,Util.getColors(1,1,1), shader,null, 0);
    currentStyle=baseStyle;
    this.x = x;
    this.bs = bs;
    this.y = y;
    this.maxWidth = width;
    fontName = font;
    this.layer = layer + 1;
    if (backgroundImage == null) {
      background = new NoSprite();
    } else {
      background = new Sprite(backgroundImage, layer, shader).setSize(width, baseStyle.size);
      background.addToBs(bs);
    }
    setText(value.get());
  }

  @Override
  public void update() {
    if(textupdater.hasChanged()) {
      setText(textupdater.get());
    }else{
      updateSymbols();
    }
    scroll -= speed;
  }

  public boolean isHidden() {
    return hidden;
  }

  @Override
  public int getX() {
    return x;
  }

  public int getMaxWidth() {
    return maxWidth;
  }

  public float getHeight() {
    return baseStyle.size;
  }

  public String getText() {
    return text;
  }

  public int getLayer() {
    return layer;
  }

  public void setLayer(int newval) {
    layer = newval;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public void hide() {
    if (hidden) {
      return;
    }
    for (Symbol s : symbols) {
      s.sprite.setHidden(true);
    }
    background.setHidden(true);
    hidden = true;
  }

  @Override
  public void show() {
    if (!hidden) {
      return;
    }
    background.setHidden(false);
    for (Symbol s : symbols) {
      s.sprite.setHidden(false);
    }
    hidden = false;
  }

  public float getFontSize() {
    return baseStyle.size;
  }

  public void setFontSize(float size) {
    baseStyle = new Style(size, baseStyle.colors, baseStyle.shader, null, 0);
    updateSymbols();
  }

  public void setText(String value) {
    if (value == null) {
      text = null;
      hide();
      return;
    }
    if (text == null) {
      show();
    }
    updateStyles(value);
    updateSymbols();
  }

  private void updateStyles(String inputText) {
    styles.clear();
    styles.add(baseStyle);
    int i=0;
    currentStyle = baseStyle;
    float maxSize = currentStyle.size;

    String[] strings = inputText.split("\\|");
    StringBuilder t = new StringBuilder();

    for (String s : strings) {
      Style st = Style.parse(s, currentStyle, i);
      if(st==null){
        st=Style.parseNewFormat(s, currentStyle, i);
      }

      if (st!=null) {
        styles.add(st);
        currentStyle=st;
        maxSize=Math.max(maxSize, st.size);
      } else {
        t.append(s);
        i+=s.length();
      }
    }
    text=t.toString();
    background.setSize(maxWidth, maxSize);
  }


  private void updateSymbols() {
    float xOffset = scroll;
    currentStyle = baseStyle;
    int sc = startChar;
    int i=0;

    for (; xOffset < maxWidth; i++) {
      int requestedChar = (i + sc) % text.length();
      for(int s=0; s<styles.size()&&styles.get(s).startChar<=requestedChar; s++){
        currentStyle=styles.get(s);
      }

      float fontSize = currentStyle.size;

      while(i>=symbols.size()){
        symbols.add(new Symbol());
      }
      Symbol symbol = symbols.get(i);

      symbol.cutoffLeft=xOffset < 0? -xOffset : 0;
      symbol.cutoffRight=maxWidth-xOffset;

      symbol.setCharacter(text.charAt(requestedChar));
      symbol.move(x + xOffset + symbol.width * .5f, y + symbol.sprite.getHeight()*0.25f- background.getHeight()*0.4f);

      if(symbol.cutoffLeft > symbol.width){
        startChar+=1;
        scroll += symbol.width;
      }

      xOffset += symbol.width;
    }

    while (symbols.size()>i) {
      Symbol unusedSymbol = symbols.remove(symbols.size()-1);
      unusedSymbol.delete();
    }

    background.setPosition(x + maxWidth / 2f, y);
  }

  @Override
  public void move(int newX, int newY) {
    newX = Math.min(newX, Constants.screenSize.x - maxWidth);
    newY = Math.min(newY,
        Constants.screenSize.y - (int) baseStyle.size / 2 - (int) (background.getHeight() * .08f));
    int dx = newX - x, dy = newY - y;
    for (var symbol : symbols) {
      symbol.move(symbol.sprite.getX() + dx, symbol.sprite.getY() + dy);
    }
    background.setPosition(newX + maxWidth / 2f, newY);
    x = newX;
    y = newY;
  }

  @Override
  public void delete() {
    for (var symbol : symbols) {
      symbol.delete();
    }
    symbols.clear();
    background.delete();
  }

  public static class StaticTextGenerator implements TextGenerator{
    private final String text;
    private boolean changed = true;

    public StaticTextGenerator(String text){
      this.text=text;
    }

    @Override
    public String get() {
      changed=false;
      return text;
    }

    @Override
    public boolean hasChanged() {
      return changed;
    }
  }

  public interface TextGenerator {

    String get();

    boolean hasChanged();
  }

  private static final class Style {

    private final float size;
    private final float[] colors;
    private final Style previous;
    final int startChar;
    final String shader;

    private Style(float size, Style previous, int startChar){
      this(size, null, null, previous, startChar);
    }

    private Style(float size, float[] colors, Style previous, int startChar){
      this(size, colors, null, previous, startChar);
    }

    private Style(float size, String shader, Style previous, int startChar){
      this(size, null, shader, previous, startChar);
    }

    private Style(float size, float[] colors, String shader, Style previous, int startChar) {
      this.size = size==-1? previous.size : size;
      this.shader = shader==null? previous.shader : shader;
      this.colors = colors==null? previous.colors : colors;
      this.previous = previous;
      this.startChar = startChar;
    }

    static Style parse(String s, Style prev, int startChar) {
      String[] split = s.split("\\.");
      for(String part : split){
        if(!part.startsWith("#")){
          return null;
        }
      }
      try {
        switch (split.length) {
          case 1 -> {
            return new Style(Integer.parseInt(split[0].substring(1)), prev, startChar);
          }
          case 3 -> {
            int red = Integer.parseInt(split[0].substring(1));
            int green = Integer.parseInt(split[1].substring(1));
            int blue = Integer.parseInt(split[2].substring(1));
            return new Style(-1, Util.getColors(red/255f, green/255f, blue/255f), prev, startChar);
          }
          default -> {
            return null;
          }
        }
      } catch (Exception e) {
        return null;
      }
    }

    static Style parseNewFormat(String s, Style prev, int startChar){
      if(Objects.equals(s, "#<")){
        Style reverted = prev.previous;
        if(reverted==null){
          return null;
        }
        return new Style(reverted.size, reverted.colors, reverted.shader, reverted.previous, startChar);
      }
      if(Objects.equals(s, "#<<")){
        Style reverted = prev;
        while(reverted.previous!=null){
          reverted = reverted.previous;
        }
        return new Style(reverted.size, reverted.colors, reverted.shader, null, startChar);
      }
      String[] split = s.split(":");
      if(split.length!=2){
        return null;
      }
      String command = split[0];
      String[] data = split[1].split(",");
      try {
        switch (command) {
          case "color" -> {
            float[] colors = new float[data.length];
            for (int i = 0; i < data.length; i++) {
              colors[i] = Float.parseFloat(data[i]);
            }
            return new Style(-1, colors, prev, startChar);
          }
          case "shader" -> {
            return new Style(-1, data[0], prev, startChar);
          }
          default -> {return null;}
        }
      }catch(Exception e){
        return null;
      }
    }

    @Override
    public String toString() {
      return "Style{"
          + "size=" + size
          + ", colors=" + Arrays.toString(colors)
          + ", startChar=" + startChar
          + ", shader='" + shader + '\''
          + ", previous=" + previous
          + '}';
    }
  }

  private class Symbol {
    final Sprite sprite;
    float width;
    char character=' ';
    float cutoffLeft=0, cutoffRight=4000;

    Symbol() {
      ImageData img = Graphics.getImage(fontName + "-SPACE");
      sprite = new Sprite(img, layer, currentStyle.shader).addToBs(bs);
    }

    void updateScale() {
      setCharacter(character);
    }

    void move(float X, float Y) {
      sprite.setPosition(X+cutoffLeft/2 - (cutoffRight<width? (width-cutoffRight)/2 : 0), Y);
    }

    void delete() {
      sprite.delete();
    }

    public char getCharacter() {
      return character;
    }

    void setCharacter(char c) {
      ImageData ogImg = Graphics.getImage(fontName + '-' + Character.getName(c));
      List<Float> tc = new ArrayList<>();
      for (float f : ogImg.textureCoordinates){
        tc.add(f);
      }
      tc.set(2, tc.get(2) + cutoffLeft / currentStyle.size *textureHeight);
      tc.set(6, tc.get(6) + cutoffLeft / currentStyle.size *textureHeight);

      tc.set(0, tc.get(2) + Math.min(tc.get(0)-tc.get(2), cutoffRight / currentStyle.size * textureHeight));
      tc.set(4, tc.get(6) + Math.min(tc.get(4)-tc.get(6), cutoffRight / currentStyle.size * textureHeight));
      var img = new ImageData(ogImg.textureName, tc);

      float[] ogUV = ogImg.textureCoordinates;
      float w = ogUV[0] - ogUV[2];
      width = w * (currentStyle.size / textureHeight);

      float[] uv = img.textureCoordinates;
      float cw = uv[0] - uv[2];
      float cutWidth = cw * (currentStyle.size / textureHeight);

      sprite.setHidden(cutWidth<0);

      sprite.setImage(img);
      sprite.setShader(currentStyle.shader);
      sprite.setSize(cutWidth, currentStyle.size);
      character = c;
      sprite.setColors(currentStyle.colors);
    }
  }
}

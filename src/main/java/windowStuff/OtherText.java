package windowStuff;

import general.Constants;
import general.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OtherText implements Text {

  private static final float textureHeight =
      128f / 4096f; //the height of a glyph sub-texture, in uv coordinates
  private int layer;
  private final String fontName;
  private final int maxWidth;
  private final String shader;
  private final SpriteBatching bs;
  private final AbstractSprite background;
  private int x, y;
  private final TextGenerator textupdater;
  private final ArrayList<Symbol> symbols = new ArrayList<>(0);
  private String text;
  private boolean hidden = false;
  private int lineCount = 1;
  private Style baseStyle, currentStyle;
  private final List<Style> styles = new ArrayList<>(0);

  public OtherText(String value, int width, int layer, float size, SpriteBatching bs) {
    this(value, "Calibri", width, 0, 0, layer, size, bs, "basic", null);
  }

  public OtherText(String value, String font, int width, int x, int y, int layer, float size,
      SpriteBatching bs) {
    this(value, font, width, x, y, layer, size, bs, "basic", null);
  }

  public OtherText(TextGenerator value, String font, int width, int x, int y, int layer,
      float size,
      SpriteBatching bs) {
    this(value, font, width, x, y, layer, size, bs, "basic", null);
  }

  public OtherText(String value, String font, int width, int x, int y, int layer, float size,
      SpriteBatching bs, String shader, String backgroundImage) {
    this(() -> value, font, width, x, y, layer, size, bs, shader, backgroundImage);
  }

  public OtherText(TextGenerator value, String font, int width, int x, int y, int layer,
      float size,
      SpriteBatching bs, String shader, String backgroundImage) {

    this.textupdater = value;
    baseStyle = new Style(size,255,255,255,null, 0);
    currentStyle=baseStyle;
    this.x = x;
    this.bs = bs;
    this.y = y;
    this.maxWidth = width;
    fontName = font;
    this.layer = layer + 1;
    this.shader = shader;
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
    setText(textupdater.get());
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
    return (lineCount + 1) * baseStyle.size;
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
    baseStyle = new Style(size, baseStyle.r, baseStyle.g, baseStyle.b, null, 0);
    arrange();
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
    updateSymbols(value);
    arrange();
  }

  private void updateSymbols(String inputText) {
    styles.clear();
    int i=0;
    currentStyle = baseStyle;

    String[] strings = inputText.split("\\|");
    StringBuilder t = new StringBuilder();

    for (String s : strings) {
      Style st = Style.parse(s, currentStyle, i);
      if (st!=null) {
        styles.add(st);
        currentStyle=st;
      } else {
        t.append(s);
        for(char c : s.toCharArray()){
          if(i>=symbols.size()){
            symbols.add(new Symbol());
          }
          symbols.get(i).setCharacter(c);
          i++;
        }
      }
    }
    text=t.toString();
    while (symbols.size()>i) {
      Symbol unusedSymbol = symbols.remove(symbols.size()-1);
      unusedSymbol.delete();
    }
  }


  private void arrange() {
    int line = 0;
    float yOffset = 0;
    var styleIt = styles.iterator();
    var nextStyle = styleIt.next();
    boolean newline = true;
    float xOffset = currentStyle.effectiveStyle.size / 4;

    for (int i = 0, size = symbols.size(); i < size; i++) {
      if(nextStyle != null && nextStyle.startChar <= i){
        currentStyle = nextStyle;
        nextStyle = styleIt.hasNext()?styleIt.next():null;
      }
      float fontSize = currentStyle.effectiveStyle.size;
      if(newline){
        newline = false;
        xOffset = fontSize / 4;
        yOffset -= fontSize * 0.7f;
        line++;
      }

      Symbol symbol = symbols.get(i);
      if (symbol.character == '\n') {
        newline=true;
      }
      symbol.move(x + xOffset + symbol.width * .5f, y + yOffset);
      xOffset += symbol.width;
      if (symbol.character == ' ') {
        float nextWordLen = 0;
        for (int j = i + 1; j < symbols.size() && symbols.get(j).character != ' '; j++) {
          nextWordLen += symbols.get(j).width;
        }
        if (xOffset > maxWidth - nextWordLen) {
          newline = true;
        }
      }
    }

    lineCount = line;

    background.setPosition(x + maxWidth / 2f, y + yOffset/2 + currentStyle.effectiveStyle.size * .1f);
    background.setSize(maxWidth, (yOffset+ currentStyle.effectiveStyle.size) * 1.08f);
    for (var symbol : symbols) {
      symbol.move(symbol.sprite.getX(),
          symbol.sprite.getY() - yOffset); //text shows above cursor...
      // kind of a pain sometimes but like you don't want cursor blocking stuff
    }
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
    background.setPosition(newX + maxWidth / 2f, newY + lineCount * baseStyle.size / 2 + baseStyle.size * .1f);
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

  @FunctionalInterface
  public interface TextGenerator {

    String get();
  }

  private static final class EffectiveStyle {

    final float size;
    final float[] colors;

    private EffectiveStyle(float size, float r, float g, float b) {
      this.size = size;
      colors = Util.getColors(r/256,g/256,b/256);
    }
  }

  private static final class Style {

    private final float size;
    private final float r, g, b;
    private final Style previous;
    final EffectiveStyle effectiveStyle;
    final int startChar;

    private Style(float size, float r, float g, float b, Style previous, int startChar) {
      this.size = size;
      this.r = r;
      this.g = g;
      this.b = b;
      this.previous = previous;
      this.startChar = startChar;
      Style baseStyle=previous;
      for(; (size ==-1 || r==-1 || g==-1 || b==-1) && baseStyle!=null; baseStyle=baseStyle.previous){
        size = size==-1 ? baseStyle.size : size;
        r = r==-1 ? baseStyle.r : r;
        g = g==-1 ? baseStyle.g : g;
        b = b==-1 ? baseStyle.b : b;
      }
      effectiveStyle = new EffectiveStyle(size,r,g,b);
    }

    static Style parse(String s, Style prev, int startChar) {
      String[] split = s.split("\\.");
      try {
        switch (split.length) {
          case 1 -> {
            if(Objects.equals(split[0], "<")){
              return prev.previous;
            }
            return new Style(Integer.parseInt(split[0].substring(1)), -1, -1, -1, prev, startChar);
          }
          case 3 -> {
            int red = Integer.parseInt(split[0].substring(1));
            int green = Integer.parseInt(split[1].substring(1));
            int blue = Integer.parseInt(split[2].substring(1));
            return new Style(-1, red, green, blue, prev, startChar);
          }
          default -> {
            return null;
          }
        }
      } catch (Exception e) {
        return null;
      }
    }
  }

  private class Symbol {
    final Sprite sprite;
    float width;
    char character;

    Symbol() {
      ImageData img = Graphics.getImage(fontName + "-SPACE");
      sprite = new Sprite(img, layer, shader).addToBs(bs);
    }

    void updateScale() {
      setCharacter(character);
    }

    void move(float X, float Y) {
      sprite.setPosition(X, Y);
    }

    void delete() {
      sprite.delete();
    }

    public char getCharacter() {
      return character;
    }

    void setCharacter(char c) {
      ImageData img = Graphics.getImage(fontName + '-' + Character.getName(c));
      float[] uv = img.textureCoordinates;
      float w = uv[0] - uv[2];
      width = w * (currentStyle.effectiveStyle.size / textureHeight);
      sprite.setImage(img);
      sprite.setSize(width, currentStyle.effectiveStyle.size);
      character = c;
      sprite.setColors(currentStyle.effectiveStyle.colors);
    }
  }
}

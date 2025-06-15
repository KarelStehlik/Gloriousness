package windowStuff;

import general.Constants;
import general.Log;
import general.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class SimpleText implements Text{

  private static final double textureHeight =
      128d / 4096d; //the height of a glyph sub-texture, in uv coordinates
  private int layer;
  private final String fontName;
  private int maxWidth;
  private final String shader;
  private final SpriteBatching bs;
  private final AbstractSprite background;
  private int x, y;
  private float fontSize;
  private float scale;
  private TextGenerator textupdater;
  private ArrayList<Symbol> symbols;
  private String text;
  private float[] colors = Util.getColors(1, 1, 1);
  private boolean hidden = false;
  private int lineCount = 1;

  public SimpleText(String value, String font, int width, int x, int y, int layer, float size,
                    SpriteBatching bs) {
    this(value, font, width, x, y, layer, size, bs, "basic", null);
  }
  public SimpleText(TextGenerator value, String font, int width, int x, int y, int layer, float size,
                    SpriteBatching bs) {
    this(value, font, width, x, y, layer, size, bs, "basic", null);
  }
  public SimpleText(String value, String font, int width, int x, int y, int layer, float size,
                    SpriteBatching bs, String shader, String backgroundImage) {
    this(()->value, font, width, x, y, layer, size, bs, shader, backgroundImage);
  }
  public SimpleText(TextGenerator value, String font, int width, int x, int y, int layer, float size,
                    SpriteBatching bs, String shader, String backgroundImage) {

    this.textupdater=value;
    fontSize = size;
    this.x = x;
    this.text=value.get();
    this.bs = bs;
    this.y = y;
    this.maxWidth = width;
    fontName = font;
    this.layer = layer + 1;
    this.shader = shader;
    symbols = new ArrayList<>(text.length());
    scale = (float) (fontSize / textureHeight);

    if (backgroundImage == null) {
      background = new NoSprite();
    } else {
      background = new Sprite(backgroundImage, width, fontSize, layer, shader);
      background.addToBs(bs);
    }

    for (char c : text.toCharArray()) {
      Symbol symbol = new Symbol(c, x, y, shader);
      symbols.add(symbol);
      bs.addSprite(symbol.sprite);
    }
    arrange();
  }
  public void update(){
    if(textupdater!=null){
      setText(textupdater.get());
    }
  }
  public boolean isHidden(){
    return hidden;
  }
  public int getX() {
    return x;
  }
  public int getMaxWidth() {
    return maxWidth;
  }
  public float getHeight(){
    return (lineCount+1)*fontSize;
  }
  public String getText(){
    return text;
  }
  public int getLayer(){
    return layer;
  }
  public void setLayer(int newval){
    layer=newval;
  }
  public int getY() {
    return y;
  }

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
    return fontSize;
  }

  public void setFontSize(float size) {
    fontSize = size;
    scale = (float) (fontSize / textureHeight);
    for (Symbol symbol : symbols) {
      symbol.updateScale();
    }
  }

  public void setText(String value) {
    if(value==null){
      text=null;
      hide();
      return;
    }
    if(Objects.equals(value, text)){
     return;
    }
    ArrayList<Symbol> newSymbols = new ArrayList<>(value.length());
    Iterator<Symbol> existing = symbols.listIterator();
    for (char c : value.toCharArray()) {
      if (existing.hasNext()) {
        Symbol symbol = existing.next();
        newSymbols.add(symbol);
        if (symbol.character != c) {
          symbol.setCharacter(c);
        }
      } else {
        Symbol symbol = new Symbol(c, x, y, shader);
        if (!hidden) {
          bs.addSprite(symbol.sprite);
        }
        newSymbols.add(symbol);
      }
      if(text==null){
        show();
      }
      text=value;
    }
    while (existing.hasNext()) {
      Symbol symbol = existing.next();
      symbol.delete();
      existing.remove();
    }
    symbols.clear();
    symbols = newSymbols;
    arrange();
  }

  public void setColors(float[] colors) {
    for (var symbol : symbols) {
      symbol.sprite.setColors(colors);
    }
    this.colors = colors;
  }

  public void move(int newX, int newY) {
    newX = Math.min(newX, Constants.screenSize.x - maxWidth);
    newY = Math.min(newY,
        Constants.screenSize.y - (int) fontSize / 2 - (int) (background.getHeight() * .08f));
    int dx = newX - x, dy = newY - y;
    for (var symbol : symbols) {
      symbol.move(symbol.sprite.getX() + dx, symbol.sprite.getY() + dy);
    }
    background.setPosition(newX + maxWidth / 2f, newY + lineCount * fontSize / 2 + fontSize * .1f);
    x = newX;
    y = newY;
  }

  private void arrange() {
    int line = 0;
    float xOffset = fontSize / 4;
    for (int i = 0, size = symbols.size(); i < size; i++) {
      Symbol symbol = symbols.get(i);
      if (symbol.character == '\n') {
        line++;
        xOffset = fontSize / 4;
      }
      symbol.move(x + xOffset + symbol.width * .5f, y - line * fontSize);
      xOffset += symbol.width;
      if (symbol.character == ' ') {
        float nextWordLen = 0;
        for (int j = i + 1; j < symbols.size() && symbols.get(j).character != ' '; j++) {
          nextWordLen += symbols.get(j).width;
        }
        if (xOffset > maxWidth - nextWordLen) {
          line++;
          xOffset = fontSize / 4;
        }
      }
    }
    if (line * fontSize >= Constants.screenSize.y * 0.9 && maxWidth < Constants.screenSize.x) {
      maxWidth = (int) (maxWidth * 1.1f);
      arrange();
    }

    lineCount = line;

    background.setPosition(x + maxWidth / 2f, y - line * fontSize / 2 + fontSize * .1f);
    background.setSize(maxWidth, (line + 1.2f) * fontSize * 1.08f);
    for (var symbol : symbols) {
      symbol.move(symbol.sprite.getX(), symbol.sprite.getY() + lineCount * fontSize); //text shows above cursor...
                                                            // kind of a pain sometimes but like you don't want cursor blocking stuff
    }
  }

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

  private class Symbol {


    final Sprite sprite;
    float width;
    char character;

    Symbol(char c, float x, float y, String shader) {
      String imageName = fontName + '-' + Character.getName(c);
      float[] uv = Graphics.getLoadedImages()
              .getImageCoordinates(Graphics.getLoadedImages().getImageId(imageName));
      float w = uv[0] - uv[2];
      width = w * scale;
      sprite = new Sprite(imageName, width, fontSize, layer, shader);
      sprite.setX(x + width / 2);
      sprite.setY(y);
      character = c;
      sprite.setColors(colors);
    }

    void updateScale() {
      String imageName = fontName + '-' + Character.getName(character);
      float[] uv = Graphics.getLoadedImages()
              .getImageCoordinates(Graphics.getLoadedImages().getImageId(imageName));
      float w = uv[0] - uv[2];
      width = w * scale;
      sprite.setSize(width, fontSize);
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
      String imageName = fontName + '-' + Character.getName(c);
      float[] uv = Graphics.getLoadedImages()
              .getImageCoordinates(Graphics.getLoadedImages().getImageId(imageName));
      float w = uv[0] - uv[2];
      width = w * scale;
      sprite.setImage(imageName);
      sprite.setSize(width, fontSize);
      character = c;
    }

  }
}

package windowStuff;

import general.Data;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Text {

  private static final double textureHeight =
      64d / 4096d; //the height of a glyph sub-texture, in uv coordinates
  float scale;

  private List<Symbol> symbols;
  private final int layer;
  public int x, y;
  private final float fontSize;
  private final String fontName;
  private final int maxWidth;
  private final String text;
  private final String shader;
  private final BatchSystem bs;
  private float[] colors = {0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,1,};
  private boolean deleted = false;

  public Text(String value, String font, int width, int x, int y, int layer, float size,
      BatchSystem bs) {
    this(value, font, width, x, x, layer, size, bs, "basic");
  }

  public Text(String value, String font, int width, int x, int y, int layer, float size,
      BatchSystem bs, String shader) {
    fontSize = size;
    this.x = x;
    this.bs = bs;
    this.y = y;
    text = value;
    this.maxWidth = width;
    fontName = font;
    this.layer = layer;
    this.shader = shader;
    symbols = new LinkedList<>();
    scale = (float) (fontSize / textureHeight);
    for (char c : value.toCharArray()) {
      Symbol s = new Symbol(c, x, y, shader);
      symbols.add(s);
      bs.addSprite(s.sprite);
    }
    arrange();
  }

  public void setText(String value){
    if(deleted){return;}
    List<Symbol> newSymbols = new LinkedList<>();
    Iterator<Symbol> existing = symbols.listIterator();
    for(char c : value.toCharArray()){
      if(existing.hasNext()){
        Symbol s = existing.next();
        newSymbols.add(s);
        if(s.character != c){
          s.setCharacter(c);
        }
      }else{
        Symbol s=new Symbol(c, x, y, shader);
        bs.addSprite(s.sprite);
        newSymbols.add(s);
      }
    }
    while(existing.hasNext()){
      Symbol s = existing.next();
      s.delete();
      existing.remove();
    }
    symbols.clear();
    symbols = newSymbols;
    arrange();
  }

  public void setColors(float[] colors) {
    for (var s : symbols) {
      s.sprite.setColors(colors);
    }
    this.colors = colors;
  }

  public void move(int newX, int newY) {
    int dx = newX - x, dy = newY - y;
    for (var symbol : symbols) {
      symbol.move(symbol.sprite.getX() + dx, symbol.sprite.getY() + dy);
    }
    x = newX;
    y = newY;
  }

  private void arrange() {
    int line = 0;
    float xOffset = 0;
    for (var symbol : symbols) {
      symbol.move(x + xOffset + symbol.width * .5f, y - line * fontSize);
      xOffset += symbol.width;
      if (symbol.character == ' ' && xOffset > maxWidth - fontSize * 2) {
        line++;
        xOffset = 0;
      }
    }
  }

  public void delete() {
    for (var symbol : symbols) {
      symbol.delete();
    }
    symbols.clear();
    deleted = true;
  }

  private class Symbol {

    final Sprite sprite;
    float width;
    char character;

    Symbol(char c, float x, float y, String shader) {
      float[] uv = Data.getImageCoordinates(fontName + '-' + Character.getName(c));
      float w = uv[0] - uv[2];
      width = w * scale;
      sprite = new Sprite(fontName + '-' + Character.getName(c), width, fontSize, layer, shader);
      sprite.setX(x + width / 2);
      sprite.setY(y);
      character = c;
      sprite.setColors(colors);
    }

    void move(float x, float y) {
      sprite.setPosition(x, y);
    }

    void delete() {
      sprite.delete();
    }

    void setCharacter(char c){
      float[] uv = Data.getImageCoordinates(fontName + '-' + Character.getName(c));
      float w = uv[0] - uv[2];
      width = w * scale;
      sprite.setImage(fontName + '-' + Character.getName(c));
      sprite.setSize(width, fontSize);
    }
  }
}

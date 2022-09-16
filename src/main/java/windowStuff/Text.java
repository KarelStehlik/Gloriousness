package windowStuff;

import general.Data;
import java.util.LinkedList;
import java.util.List;

public class Text {

  private static final double textureHeight =
      64d / 4096d; //the height of a glyph sub-texture, in uv coordinates

  private final List<Symbol> symbols;
  private final int layer;
  public int x, y;
  private final float fontSize;
  private final String fontName;
  private final int maxWidth;
  private final String text;

  public Text(String value, String font, int width, int x, int y, int layer, float size,
      BatchSystem bs) {
    this(value, font, width, x, x, layer, size, bs, "basic");
  }

  public Text(String value, String font, int width, int x, int y, int layer, float size,
      BatchSystem bs, String shader) {
    fontSize = size;
    this.x = x;
    this.y = y;
    text = value;
    this.maxWidth = width;
    fontName = font;
    this.layer = layer;
    symbols = new LinkedList<>();
    for (char c : value.toCharArray()) {
      Symbol s = new Symbol(c, x, y, shader);
      symbols.add(s);
      bs.addSprite(s.sprite);
    }
    arrange();
  }

  public void setColors(float[] colors) {
    for (var s : symbols) {
      s.sprite.setColors(colors);
    }
  }

  public void move(int newX, int newY) {
    int dx = newX - x, dy = newY - y;
    for (var symbol : symbols) {
      symbol.move(symbol.sprite.x + dx, symbol.sprite.y + dy);
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
  }

  private class Symbol {

    final Sprite sprite;
    float width;
    char character;

    Symbol(char c, float x, float y, String shader) {
      List<Float> uv = Data.getImageCoordinates(fontName + '-' + Character.getName(c));
      float w = uv.get(0) - uv.get(2);
      float scale = (float) (fontSize / textureHeight);
      width = w * scale;
      sprite = new Sprite(fontName + '-' + Character.getName(c), width, fontSize, layer, shader);
      sprite.x = x + width / 2;
      sprite.y = y;
      character = c;
    }

    void move(float x, float y) {
      sprite.setPosition(x, y);
    }

    void delete() {
      sprite.delete();
    }
  }
}

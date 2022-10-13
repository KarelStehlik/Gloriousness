package Game;

public enum DamageType {
  TRUE(""), PHYSICAL("physicalResist"), MAGIC("magicResist");

  public final String resistanceName;

  DamageType(String s) {
    resistanceName = s;
  }
}

package xyz.telegram.depinalliance.common.constans;

/**
 * @author holden on 13-Aug-2024
 */
public class Enums {

  public enum Config {
    POINT_REF(1);
    private final int type;

    Config(int type) {
      this.type = type;
    }

    public int getType() {
      return this.type;
    }
  }
}

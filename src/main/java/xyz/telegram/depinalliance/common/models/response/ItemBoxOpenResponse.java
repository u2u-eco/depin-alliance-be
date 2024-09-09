package xyz.telegram.depinalliance.common.models.response;

/**
 * @author holden on 09-Sep-2024
 */
public class ItemBoxOpenResponse {
  public String type;
  public String name;

  public ItemBoxOpenResponse(String type, String name) {
    this.type = type;
    this.name = name;
  }

  @Override
  public String toString() {
    return type + " " + name;
  }
}

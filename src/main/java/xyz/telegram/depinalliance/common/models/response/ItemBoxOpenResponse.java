package xyz.telegram.depinalliance.common.models.response;

import java.math.BigDecimal;

/**
 * @author holden on 09-Sep-2024
 */
public class ItemBoxOpenResponse {
  public String type;
  public String name;
  public BigDecimal point;

  public ItemBoxOpenResponse(String type, String name, BigDecimal point) {
    this.type = type;
    this.name = name;
    this.point = point;
  }

  @Override
  public String toString() {
    return type + " " + name;
  }
}

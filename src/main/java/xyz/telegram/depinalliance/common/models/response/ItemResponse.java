package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class ItemResponse {
  public String name;
  public String code;
  public Enums.ItemType type;
  public BigDecimal miningPower;
  public BigDecimal price;
  public String image;

  public ItemResponse(String name, String code, Enums.ItemType type, BigDecimal miningPower, BigDecimal price,
    String image) {
    this.name = name;
    this.code = code;
    this.type = type;
    this.miningPower = Utils.stripDecimalZeros(miningPower);
    this.price = Utils.stripDecimalZeros(price);
    this.image = image;
  }
}

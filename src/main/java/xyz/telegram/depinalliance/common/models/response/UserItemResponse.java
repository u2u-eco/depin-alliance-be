package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 27-Aug-2024
 */
public class UserItemResponse {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Long id;
  public String name;
  public String code;
  public Enums.ItemType type;
  public BigDecimal miningPower;
  public String image;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Long totalItem;
  public BigDecimal price;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean isCanSell;

  public UserItemResponse(String name, String code, Enums.ItemType type, BigDecimal miningPower, String image,
    BigDecimal price, Long totalItem, Boolean isCanSell) {
    this.name = name;
    this.code = code;
    this.type = type;
    this.miningPower = Utils.stripDecimalZeros(miningPower);
    this.image = image;
    this.totalItem = totalItem;
    this.price = Utils.stripDecimalZeros(price);
    this.isCanSell = isCanSell;
  }

  public UserItemResponse(Long id, String name, String code, Enums.ItemType type, BigDecimal miningPower, String image,
    BigDecimal price) {
    this.id = id;
    this.name = name;
    this.code = code;
    this.type = type;
    this.miningPower = miningPower;
    this.image = image;
    this.price = price;
  }
}

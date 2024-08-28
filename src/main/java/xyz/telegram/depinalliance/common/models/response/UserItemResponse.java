package xyz.telegram.depinalliance.common.models.response;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import xyz.telegram.depinalliance.common.constans.Enums;

import java.math.BigDecimal;

/**
 * @author holden on 27-Aug-2024
 */
public class UserItemResponse {
  public Long id;
  public String name;
  public String code;
  public Enums.ItemType type;
  public BigDecimal miningPower;
  public String image;

  public UserItemResponse(Long id, @ProjectedFieldName("item.name") String name, @ProjectedFieldName("item.code") String code,
    @ProjectedFieldName("item.type") Enums.ItemType type,
    @ProjectedFieldName("item.miningPower") BigDecimal miningPower, @ProjectedFieldName("item.image") String image) {
    this.name = name;
    this.code = code;
    this.type = type;
    this.miningPower = miningPower;
    this.image = image;
    this.id = id;
  }
}

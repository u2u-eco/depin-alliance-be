package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class DetectDeviceResponse {
  public String type;
  public String name;
  public BigDecimal point;

  public DetectDeviceResponse(Enums.ItemType type, String name, BigDecimal point) {
    this.type = type.name();
    this.name = name;
    this.point = Utils.stripDecimalZeros(point);
  }
}

package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 03-Sep-2024
 */
public class ClaimResponse {
  public BigDecimal point;
  public BigDecimal bonusReward;

  public ClaimResponse(BigDecimal point, BigDecimal bonusReward) {
    this.point = Utils.stripDecimalZeros(point);
    this.bonusReward = Utils.stripDecimalZeros(bonusReward);
  }
}

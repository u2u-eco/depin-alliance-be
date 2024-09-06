package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class RankingResponse {
  public String username;
  public String avatar;
  public BigDecimal miningPowerReal;

  public RankingResponse(String username, String avatar, BigDecimal miningPowerReal) {
    this.username = username;
    this.avatar = avatar;
    this.miningPowerReal = Utils.stripDecimalZeros(miningPowerReal);
  }
}

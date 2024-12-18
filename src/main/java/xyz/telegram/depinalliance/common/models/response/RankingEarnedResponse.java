package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class RankingEarnedResponse {
  public String username;
  public String avatar;
  public BigDecimal pointEarned;

  public RankingEarnedResponse(String username, String avatar, BigDecimal pointEarned) {
    this.username = username;
    this.avatar = avatar;
    this.pointEarned = Utils.stripDecimalZeros(pointEarned);
  }
}

package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class RankingAirdropResponse {
  public String username;
  public String avatar;
  public BigDecimal pointEarned1;

  public RankingAirdropResponse(String username, String avatar, BigDecimal pointEarned1) {
    this.username = username;
    this.avatar = avatar;
    this.pointEarned1 = Utils.stripDecimalZeros(pointEarned1);
  }
}

package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;

/**
 * @author holden on 29-Aug-2024
 */
public class MemberLeagueResponse {
  public Long id;
  public String username;
  public String avatar;
  public BigDecimal miningPower;

  public MemberLeagueResponse(Long id, String username, String avatar, BigDecimal miningPowerReal) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.miningPower = Utils.stripDecimalZeros(miningPowerReal);
  }
}

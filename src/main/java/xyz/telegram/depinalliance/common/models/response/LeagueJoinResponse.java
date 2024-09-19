package xyz.telegram.depinalliance.common.models.response;

import java.math.BigDecimal;

/**
 * @author holden on 19-Sep-2024
 */
public class LeagueJoinResponse {
  public String username;
  public Long userId;
  public BigDecimal miningPower;
  public String avatar;

  public LeagueJoinResponse(String username, Long userId, BigDecimal miningPower, String avatar) {
    this.username = username;
    this.userId = userId;
    this.miningPower = miningPower;
    this.avatar = avatar;
  }
}

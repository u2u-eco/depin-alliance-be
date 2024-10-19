package xyz.telegram.depinalliance.common.models.response;

import java.math.BigDecimal;

/**
 * @author holden on 18-Oct-2024
 */
public class WorldMapRewardResponse {
  public BigDecimal reward;
  public BigDecimal dailyCombo;

  public WorldMapRewardResponse(BigDecimal reward, BigDecimal dailyCombo) {
    this.reward = reward;
    this.dailyCombo = dailyCombo;
  }
}

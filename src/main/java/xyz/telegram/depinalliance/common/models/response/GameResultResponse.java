package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.GameResult;

/**
 * @author holden on 04-Oct-2024
 */
public class GameResultResponse {
  public Long id;
  public Enums.GameType type;
  public boolean isCompleted;
  public String gameDetail;
  public String latitude;
  public String longitude;
  public Long createdAt;
  public Long updatedAt;

  public GameResultResponse(GameResult gameResult) {
    this.id = gameResult.id;
    this.type = gameResult.type;
    this.isCompleted = gameResult.isCompleted;
    this.gameDetail = gameResult.gameDetail;
    this.latitude = gameResult.latitude;
    this.longitude = gameResult.longitude;
    this.createdAt = gameResult.createdAt;
    this.updatedAt = gameResult.updatedAt;
  }
}

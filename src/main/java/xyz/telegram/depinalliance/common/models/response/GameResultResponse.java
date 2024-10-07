package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 04-Oct-2024
 */
public class GameResultResponse {
  public Long id;
//  public Enums.GameType type;
  public boolean isCompleted;
  public String gameDetail;
  public String latitude;
  public String longitude;
  public Long createdAt;
  public Long updatedAt;
}

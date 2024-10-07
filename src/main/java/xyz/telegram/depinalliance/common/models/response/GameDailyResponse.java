package xyz.telegram.depinalliance.common.models.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author holden on 07-Oct-2024
 */
public class GameDailyResponse {
  public GameItemResponse agency;
  public GameItemResponse tool;
  public GameItemResponse continent;
  public BigDecimal reward;
  public boolean isCompleted = false;
  public Long date;
  public long time;

  public List<GameResultResponse> results;
}

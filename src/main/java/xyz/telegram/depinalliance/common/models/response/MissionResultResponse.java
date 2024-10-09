package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 04-Oct-2024
 */
public class MissionResultResponse {
  public Long id;
  public Enums.MissionDailyType type;
  public boolean isCompleted;
  @JsonIgnore
  public String gameDetail;
  public String latitude;
  public String longitude;
  public String locationName;
  public Long createdAt;
  public Long updatedAt;

  public MissionResultResponse(Long id, Enums.MissionDailyType type, boolean isCompleted, String gameDetail,
    String latitude, String longitude, String locationName, Long createdAt, Long updatedAt) {
    this.id = id;
    this.type = type;
    this.isCompleted = isCompleted;
    this.gameDetail = gameDetail;
    this.latitude = latitude;
    this.longitude = longitude;
    this.locationName = locationName;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}

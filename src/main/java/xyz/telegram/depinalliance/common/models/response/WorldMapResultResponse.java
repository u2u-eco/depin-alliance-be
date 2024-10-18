package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 04-Oct-2024
 */
public class WorldMapResultResponse {
  public Long id;
  public Enums.WorldMapType type;
  public boolean isCompleted;
  @JsonIgnore
  public String gameDetail;
  public String location;
  public String locationName;
  public Long createdAt;
  public Long updatedAt;

  public WorldMapResultResponse(Long id, Enums.WorldMapType type, boolean isCompleted, String gameDetail,
    String location, String locationName, Long createdAt, Long updatedAt) {
    this.id = id;
    this.type = type;
    this.isCompleted = isCompleted;
    this.gameDetail = gameDetail;
    this.location = location;
    this.locationName = locationName;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}

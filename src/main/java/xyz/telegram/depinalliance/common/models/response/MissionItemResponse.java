package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.WorldMapItem;

/**
 * @author holden on 07-Oct-2024
 */
public class MissionItemResponse {
  @JsonIgnore
  public Long id;
  public String name;
  public String code;
  public String description;
  public Enums.MissionItemType type;
  public String image;

  public MissionItemResponse(String name, String code, String description, Enums.MissionItemType type, String image) {
    this.name = name;
    this.code = code;
    this.description = description;
    this.type = type;
    this.image = image;
  }

  public MissionItemResponse(WorldMapItem item) {
    this.id = item.id;
    this.name = item.name;
    this.code = item.code;
    this.description = item.description;
    this.type = item.type;
    this.image = item.image;
  }

}

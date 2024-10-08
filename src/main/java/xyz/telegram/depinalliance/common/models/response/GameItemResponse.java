package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.GameItem;

/**
 * @author holden on 07-Oct-2024
 */
public class GameItemResponse {
  public String name;
  public String code;
  public String description;
  public Enums.GameItemType type;
  public String image;

  public GameItemResponse(GameItem item) {
    this.name = item.name;
    this.code = item.code;
    this.description = item.description;
    this.type = item.type;
    this.image = item.image;
  }

  public GameItemResponse(String name, String code, String description, Enums.GameItemType type, String image) {
    this.name = name;
    this.code = code;
    this.description = description;
    this.type = type;
    this.image = image;
  }
}

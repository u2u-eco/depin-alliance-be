package xyz.telegram.depinalliance.common.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author holden on 03-Sep-2024
 */
public class PartnerResponse {
  public String name;
  public String description;
  public String rewards;
  public long participants;
  public String image;
  public List<UserMissionResponse> missions = new ArrayList<>();

  public PartnerResponse(String name, String description, String rewards, long participants, String image) {
    this.name = name;
    this.description = description;
    this.rewards = rewards;
    this.participants = participants;
    this.image = image;
  }
}

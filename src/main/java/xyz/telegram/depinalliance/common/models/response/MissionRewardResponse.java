package xyz.telegram.depinalliance.common.models.response;

/**
 * @author holden on 23-Sep-2024
 */
public class MissionRewardResponse {
  public Long amount;
  public String name;
  public String image;

  public MissionRewardResponse(Long amount, String name, String image) {
    this.amount = amount;
    this.name = name;
    this.image = image;
  }
}

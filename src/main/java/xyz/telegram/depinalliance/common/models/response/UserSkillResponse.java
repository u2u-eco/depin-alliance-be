package xyz.telegram.depinalliance.common.models.response;

public class UserSkillResponse {
  public Long skillId;
  public String name;
  public String image;
  public Long levelCurrent;
  public Long maxLevel;
  public Long timeWaiting;

  public UserSkillResponse(Long skillId, String name, String image, Long levelCurrent, Long maxLevel, Long timeWaiting) {
    this.skillId = skillId;
    this.name = name;
    this.image = image;
    this.levelCurrent = levelCurrent;
    this.maxLevel = maxLevel;
    this.timeWaiting = timeWaiting;
  }
}

package xyz.telegram.depinalliance.common.models.response;

public class UserSkillResponse {
    public Long skillId;
    public String name;
    public Long levelCurrent;
    public Long maxLevel;
    public Long timeWaiting;

    public UserSkillResponse(Long skillId, String name, Long levelCurrent, Long maxLevel, Long timeWaiting) {
        this.skillId = skillId;
        this.name = name;
        this.levelCurrent = levelCurrent;
        this.maxLevel = maxLevel;
        this.timeWaiting = timeWaiting;
    }
}

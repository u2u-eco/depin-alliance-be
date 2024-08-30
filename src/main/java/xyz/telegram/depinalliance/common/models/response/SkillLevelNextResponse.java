package xyz.telegram.depinalliance.common.models.response;

import java.math.BigDecimal;

public class SkillLevelNextResponse {
    public Long skillId;
    public String name;
    public Long levelCurrent;
    public Long levelUpgrade;
    public BigDecimal feeUpgrade;
    public BigDecimal rateEffect;
}

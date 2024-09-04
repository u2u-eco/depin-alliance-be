package xyz.telegram.depinalliance.common.models.response;

import xyz.telegram.depinalliance.common.constans.Enums;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class UserInfoResponse {
  public String username;
  public String code;
  public Enums.UserStatus status;
  public BigDecimal miningPower;
  public BigDecimal maximumPower;
  public BigDecimal point;
  public BigDecimal pointUnClaimed;
  public BigDecimal xp;
  public BigDecimal xpLevelFrom;
  public BigDecimal xpLevelTo;
  public BigDecimal pointSkill;
  public String avatar;
  public Integer totalDevice;
  public long level;
  public long lastLoginTime;
  public Long timeStartMining;
  public long lastCheckin;
}

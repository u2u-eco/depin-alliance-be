package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.League;
import xyz.telegram.depinalliance.entities.User;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author holden on 29-Aug-2024
 */
public class LeagueResponse {
  public String code;
  public String name;
  public String avatar;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String inviteLink;
  public long totalContributors;
  public BigDecimal totalMining;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean isOwner;
  public long level;
  public BigDecimal xp;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean isPendingRequest;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String adminUsername;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public BigDecimal adminMiningPower;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String adminAvatar;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public BigDecimal profit;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public BigDecimal point;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String role;

  public LeagueResponse(String code, String name, String avatar, Long totalContributors, BigDecimal totalMining,
    Long leagueId) {
    this.code = code;
    this.name = name;
    this.totalContributors = totalContributors;
    this.totalMining = Utils.stripDecimalZeros(totalMining);
    this.avatar = avatar;
    this.isPendingRequest = leagueId != null;
  }

  public LeagueResponse(League league, String userCode) {
    this.code = league.code;
    this.name = league.name;
    this.totalContributors = league.totalContributors;
    this.totalMining = Utils.stripDecimalZeros(league.totalMining);
    this.avatar = league.avatar;
    this.inviteLink = userCode + "_" + league.code;
    this.xp = Utils.stripDecimalZeros(league.xp);
  }

  public LeagueResponse(League league, User user) {
    this.code = league.code;
    this.name = league.name;
    this.totalContributors = league.totalContributors;
    this.totalMining = Utils.stripDecimalZeros(league.totalMining);
    this.avatar = league.avatar;
    this.inviteLink = user.code + "_" + league.code;
    this.isOwner = Objects.equals(league.user.id, user.id);
    User userAdmin = league.user;
    if (userAdmin != null) {
      this.adminUsername = userAdmin.username;
      this.adminMiningPower = userAdmin.miningPowerReal;
      this.adminAvatar = userAdmin.avatar;
    }
    this.xp = Utils.stripDecimalZeros(league.xp);
  }

  public LeagueResponse(League league, boolean isPendingRequest) {
    this.code = league.code;
    this.name = league.name;
    this.totalContributors = league.totalContributors;
    this.totalMining = Utils.stripDecimalZeros(league.totalMining);
    this.avatar = league.avatar;
    this.xp = Utils.stripDecimalZeros(league.xp);
    //    this.level = league.level.id;
    this.isPendingRequest = isPendingRequest;
    this.point = Utils.stripDecimalZeros(league.point);
    this.profit = Utils.stripDecimalZeros(league.profit);
  }
}

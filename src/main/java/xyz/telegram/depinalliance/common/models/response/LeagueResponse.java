package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.League;
import xyz.telegram.depinalliance.entities.User;

import java.math.BigDecimal;

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

  public LeagueResponse(String code, String name, String avatar, Long totalContributors, BigDecimal totalMining) {
    this.code = code;
    this.name = name;
    this.totalContributors = totalContributors;
    this.totalMining = Utils.stripDecimalZeros(totalMining);
    this.avatar = avatar;
  }

  public LeagueResponse(League league, String userCode) {
    this.code = league.code;
    this.name = league.name;
    this.totalContributors = league.totalContributors;
    this.totalMining = Utils.stripDecimalZeros(league.totalMining);
    this.avatar = league.avatar;
    this.inviteLink = userCode + "_" + league.code;
    this.xp = Utils.stripDecimalZeros(league.xp);
    this.level = league.level.id;
  }

  public LeagueResponse(League league, User user) {
    this.code = league.code;
    this.name = league.name;
    this.totalContributors = league.totalContributors;
    this.totalMining = Utils.stripDecimalZeros(league.totalMining);
    this.avatar = league.avatar;
    this.inviteLink = user.code + "_" + league.code;
    this.isOwner = league.user.id == user.id;
    this.xp = Utils.stripDecimalZeros(league.xp);
    this.level = league.level.id;
  }
}

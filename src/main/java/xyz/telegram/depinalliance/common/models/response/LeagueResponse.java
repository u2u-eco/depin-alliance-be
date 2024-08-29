package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
  public BigDecimal totalMining = BigDecimal.ZERO;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Boolean isOwner;

  public LeagueResponse(String code, String name, String avatar, long totalContributors, BigDecimal totalMining) {
    this.code = code;
    this.name = name;
    this.totalContributors = totalContributors;
    this.totalMining = totalMining;
    this.avatar = avatar;
  }

  public LeagueResponse(League league, String userCode) {
    this.code = league.code;
    this.name = league.name;
    this.totalContributors = league.totalContributors;
    this.totalMining = league.totalMining;
    this.avatar = league.avatar;
    this.inviteLink = userCode + "_" + league.code;
  }

  public LeagueResponse(League league, User user) {
    this.code = league.code;
    this.name = league.name;
    this.totalContributors = league.totalContributors;
    this.totalMining = league.totalMining;
    this.avatar = league.avatar;
    this.inviteLink = user.code + "_" + league.code;
    this.isOwner = league.user.id == user.id;
  }
}

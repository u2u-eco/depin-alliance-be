package xyz.telegram.depinalliance.common.models.response;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;

import java.math.BigDecimal;

/**
 * @author holden on 30-Sep-2024
 */
public class MemberLeagueDetail {
  public Long id;
  public String username;
  public String avatar;
  public BigDecimal totalEarned;
  public BigDecimal totalProfit;
  public BigDecimal totalFunding;
  public BigDecimal contributed;
  public String role;

  public MemberLeagueDetail(@ProjectedFieldName("user.id") Long id,
    @ProjectedFieldName("user.username") String username, @ProjectedFieldName("user.avatar") String avatar,
    @ProjectedFieldName("user.pointEarned") BigDecimal totalEarned,
    @ProjectedFieldName("user.miningPowerReal") BigDecimal totalProfit,BigDecimal pointFunding,
    BigDecimal contributeProfit, String leagueRole) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.totalEarned = totalEarned;
    this.totalProfit = totalProfit;
    this.totalFunding = pointFunding;
    this.contributed = contributeProfit;
    this.role = leagueRole;
  }
}

package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 20-Sep-2024
 */
@Entity
@Table(name = "league_members")
public class LeagueMember extends BaseEntity {
  @Id
  public String id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  public League league;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @Column(name = "is_admin")
  public boolean isAdmin = false;
  @Column(name = "league_role")
  public String leagueRole;
  @Column(name = "point_funding", scale = 18, precision = 29, columnDefinition = "numeric(29, 18) DEFAULT 0")
  public BigDecimal pointFunding = BigDecimal.ZERO;
  @Column(name = "contribute_profit", scale = 18, precision = 29, columnDefinition = "numeric(29, 18) DEFAULT 0")
  public BigDecimal contributeProfit = BigDecimal.ZERO;

  public static LeagueMember create(League league, User user) {
    return create(league, user, false, null);
  }

  public static LeagueMember create(League league, User user, boolean isAdmin, String role) {
    LeagueMember leagueMember = new LeagueMember();
    leagueMember.id = league.id + "_" + user.id;
    leagueMember.league = league;
    leagueMember.user = user;
    leagueMember.isAdmin = isAdmin;
    leagueMember.leagueRole = role;
    leagueMember.create();
    leagueMember.persist();
    return leagueMember;
  }

  public static LeagueMember findByUserIdAndLeagueId(long userId, long leagueId) {
    return find("league.id = ?1 and user.id = ?2", leagueId, userId).firstResult();
  }

  public static boolean updateMember(String query, Map<String, Object> params) {
    return update(query, params) > 0;
  }

  public static boolean updatePointFundingLeague(long userId, BigDecimal point) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("point", point);
    String sql = "pointFunding = pointFunding + :point  where user.id = :userId and pointFunding + :point >= 0";
    return updateMember(sql, params);
  }

  public static boolean updateLeagueContributeProfit(long userId, BigDecimal profit) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("profit", profit);
    String sql = "contributeProfit = contributeProfit + :profit where user.id = :userId and contributeProfit + :profit >= 0";
    return updateMember(sql, params);
  }
}

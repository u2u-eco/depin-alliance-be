package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.common.models.response.ResponsePage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 28-Aug-2024
 */
@Entity
@Table(name = "league")
public class League extends BaseEntity {
  @Id
  @SequenceGenerator(name = "leagueSequence", sequenceName = "league_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leagueSequence")
  public Long id;
  public String name;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true)
  public User user;
  @Column(unique = true)
  public String code;
  public String avatar;
  @Column(unique = true)
  public String nameNormalize;
  @Column(name = "total_contributors")
  public long totalContributors = 0;
  @Column(name = "total_mining")
  public BigDecimal totalMining = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;
  @Column(name = "point", scale = 18, precision = 29, columnDefinition = "numeric(29, 18) DEFAULT 0")
  public BigDecimal point = BigDecimal.ZERO;
  @Column(name = "profit", scale = 18, precision = 29, columnDefinition = "numeric(29, 18) DEFAULT 0")
  public BigDecimal profit = BigDecimal.ZERO;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "level_id")
  public LeagueLevel level;
  @Column(name = "is_active", columnDefinition = "boolean default true")
  public boolean isActive = true;

  public League() {
  }

  public League(Long id) {
    this.id = id;
  }

  public static void createLeague(League league) {
    league.create();
    league.nameNormalize = league.name.toLowerCase();
    league.code = getCodeLeague();
    league.persistAndFlush();
  }

  public static boolean updateObject(String query, Map<String, Object> params) {
    return update(query, params) > 0;
  }

  public static boolean updateProfit(Long leagueId, BigDecimal profit) {
    Map<String, Object> params = new HashMap<>();
    params.put("profit", profit);
    params.put("id", leagueId);
    return updateObject("profit = profit + :profit where id = :id and profit + :profit >= 0", params);
  }

  public static boolean updatePoint(Long leagueId, BigDecimal profit) {
    Map<String, Object> params = new HashMap<>();
    params.put("point", profit);
    params.put("id", leagueId);
    return updateObject("point = point + :point where id = :id and point + :point >= 0", params);
  }

  public static String getCodeLeague() {
    while (true) {
      String code = RandomStringUtils.randomAlphanumeric(6);
      if (countByCode(code) <= 0)
        return code;
    }
  }

  public static League findByCode(String code) {
    return find("code =?1 and isActive = true", code).firstResult();
  }

  public static long countByCode(String code) {
    return count("code", code);
  }

  public static long countByNameNormalize(String nameNormalize) {
    return count("nameNormalize", nameNormalize.toLowerCase());
  }

  public static ResponsePage<LeagueResponse> findByPagingAndNameSearchAndUserId(PagingParameters pageable,
    String nameSearch, long userId) {
    String sql = "select l.code, l.name, l.avatar, l.totalContributors, l.profit, r.league.id from League l left join LeagueJoinRequest r on l.id = r.league.id and r.status = :status and r.user.id = :userId";
    Map<String, Object> params = new HashMap<>();
    params.put("status", Enums.LeagueJoinRequestStatus.PENDING);
    params.put("userId", userId);
    sql += " where l.isActive = true";
    if (StringUtils.isNotBlank(nameSearch)) {
      sql += " and l.nameNormalize like :nameSearch";
      params.put("nameSearch", "%" + nameSearch.toLowerCase().trim() + "%");
    }
    PanacheQuery<PanacheEntityBase> panacheQuery = find(sql,
      Sort.descending("l.profit", "l.totalContributors").and("l.createdAt", Sort.Direction.Ascending), params);
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(LeagueResponse.class).list(), pageable,
      panacheQuery.count());
  }

  public static ResponsePage<LeagueResponse> findByPagingAndNameSearch(PagingParameters pageable, String nameSearch) {
    String sql = "select l.code, l.name, l.avatar, l.totalContributors, l.profit from League l ";
    Map<String, Object> params = new HashMap<>();
    sql += " where l.isActive = true";
    if (StringUtils.isNotBlank(nameSearch)) {
      sql += " and l.nameNormalize like :nameSearch";
      params.put("nameSearch", "%" + nameSearch.toLowerCase().trim() + "%");
    }
    PanacheQuery<PanacheEntityBase> panacheQuery = find(sql,
      Sort.descending("l.profit", "l.totalContributors").and("l.createdAt", Sort.Direction.Ascending), params);
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(LeagueResponse.class).list(), pageable,
      panacheQuery.count());
  }

  public static boolean updateLevel(Long id, Long levelNew) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("levelNew", levelNew);
    return update("level.id = :levelNew where id = :id and level.id < :levelNew ", params) > 0;
  }
}

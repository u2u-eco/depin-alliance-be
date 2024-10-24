package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import org.apache.commons.lang3.RandomStringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.FriendResponse;
import xyz.telegram.depinalliance.common.models.response.ResponsePage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 25-Jul-2024
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {
  @Id
  public Long id;
  @Column(name = "username")
  public String username;
  @Column(name = "point", scale = 18, precision = 29)
  public BigDecimal point = BigDecimal.ZERO;
  @Column(name = "point_skill", scale = 18, precision = 29)
  public BigDecimal pointSkill = BigDecimal.ZERO;
  @Column(name = "point_un_claimed", scale = 18, precision = 29)
  public BigDecimal pointUnClaimed = BigDecimal.ZERO;
  @Column(name = "point_claimed", scale = 18, precision = 29)
  public BigDecimal pointClaimed = BigDecimal.ZERO;
  @Column(name = "point_used", scale = 18, precision = 29, columnDefinition = "numeric(29, 18) DEFAULT 0")
  public BigDecimal pointUsed = BigDecimal.ZERO;
  @Column(name = "point_earned", scale = 18, precision = 29, columnDefinition = "numeric(29, 18) DEFAULT 0")
  public BigDecimal pointEarned = BigDecimal.ZERO;
  @Column(name = "claim_number", columnDefinition = "bigint DEFAULT 0")
  public long claimNumber = 0;
  @Column(name = "point_bonus", scale = 18, precision = 29)
  public BigDecimal pointBonus = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;
  @Column(name = "mining_power", scale = 18, precision = 29)
  public BigDecimal miningPower = BigDecimal.ZERO;
  @Column(name = "mining_power_real", scale = 18, precision = 29)
  public BigDecimal miningPowerReal = BigDecimal.ZERO;
  @Column(name = "maximum_power", scale = 18, precision = 29)
  public BigDecimal maximumPower = BigDecimal.ZERO;
  @Column(name = "rate_mining", scale = 18, precision = 29)
  public BigDecimal rateMining = BigDecimal.ONE;
  @Column(name = "rate_purchase", scale = 18, precision = 29)
  public BigDecimal ratePurchase = BigDecimal.ONE;
  @Column(name = "rate_reward", scale = 18, precision = 29)
  public BigDecimal rateReward = BigDecimal.ONE;
  @Column(name = "rate_count_down", scale = 18, precision = 29)
  public BigDecimal rateCountDown = BigDecimal.ONE;
  @Column(name = "rate_capacity", scale = 18, precision = 29)
  public BigDecimal rateCapacity = BigDecimal.ONE;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ref_id")
  public User ref;
  @Column(name = "point_ref", scale = 18, precision = 29)
  public BigDecimal pointRef = BigDecimal.ZERO;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "level_id")
  public Level level;
  @Column(unique = true)
  public String code;
  @Column(name = "time_start_mining")
  public long timeStartMining = 0L;
  @Column(name = "last_login_time")
  public long lastLoginTime = 0L;
  public String ip;
  @Column(name = "first_login_time")
  public Long firstLoginTime = 0L;
  @Column(name = "start_check_in")
  public long startCheckIn = 0L;
  @Column(name = "last_check_in")
  public long lastCheckIn = 0L;
  @Column
  public String avatar;
  @Column
  public Enums.UserStatus status;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  public League league;
  @Column(name = "joined_league_at")
  public Long joinedLeagueAt;
  @Column(name = "total_device")
  public Integer totalDevice = 0;
  @Column(name = "total_friend")
  public long totalFriend = 0;
  @Column(name = "detect_device")
  public String detectDevice;
  @Column(name = "device_platform")
  public String devicePlatform;
  @Column(name = "device_model", columnDefinition = "text")
  public String deviceModel;
  @Column(name = "is_premium")
  public Boolean isPremium;
  @Column(name = "ref_percent_claim", scale = 18, precision = 29, columnDefinition = "numeric(29, 18) DEFAULT 0")
  public BigDecimal refPercentClaim = BigDecimal.ZERO;
  @Column(name = "enable_notification", columnDefinition = "boolean default true")
  public boolean enableNotification = true;
  @Column(name = "enable_music_theme", columnDefinition = "boolean default true")
  public boolean enableMusicTheme = true;
  @Column(name = "enable_sound_effect", columnDefinition = "boolean default true")
  public boolean enableSoundEffect = true;
  @Column(name = "address_evm")
  public String addressEvm;
  @Column(name = "address_ton")
  public String addressTon;
  @Column(name = "connect_by_evm")
  public String connectByEvm;
  @Column(name = "connect_by_ton")
  public String connectByTon;
  @Column(name = "skip_tutorial_main" , columnDefinition = "boolean default false")
  public Boolean skipTutorialMain;
  @Column(name = "skip_tutorial_world_map" , columnDefinition = "boolean default false")
  public Boolean skipTutorialWorldMap;

  public User(Long id) {
    this.id = id;
  }

  public User() {
  }

  public static User createUser(User user) {
    user.create();
    user.persist();
    user.code = getCodeUser();
    return user;
  }

  public static String getCodeUser() {
    while (true) {
      String code = RandomStringUtils.randomAlphanumeric(10);
      if (countByCode(code) <= 0)
        return code;
    }
  }

  public static User findByCode(String code) {
    return find("code", code).firstResult();
  }

  public static long countByCode(String code) {
    try {
      return count("code", code);
    } catch (Exception e) {
      throw e;
    }
  }

  public static boolean updateUser(String query, Map<String, Object> params) {
    return update(query, params) > 0;
  }

  public static boolean updatePointUser(long id, BigDecimal point) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("point", point);
    String sql;
    if (point.compareTo(BigDecimal.ZERO) < 0) {
      sql = "pointUsed = pointUsed + :point * -1, ";
    } else {
      sql = "pointEarned = pointEarned + :point, ";
    }
    return updateUser(sql + "point = point + :point where id = :id and point + :point >= 0", params);
  }

//  public static boolean updatePointFundingLeague(long id, BigDecimal point) {
//    Map<String, Object> params = new HashMap<>();
//    params.put("id", id);
//    params.put("point", point);
//    String sql = "pointUsed = pointUsed + :point, point = point - :point, leaguePointFunding = leaguePointFunding + :point  where id = :id and point - :point >= 0";
//    return updateUser(sql, params);
//  }
//
//  public static boolean updateLeagueContributeProfit(long id, BigDecimal profit) {
//    Map<String, Object> params = new HashMap<>();
//    params.put("id", id);
//    params.put("profit", profit);
//    String sql = "leagueContributeProfit = leagueContributeProfit + :profit where id = :id and leagueContributeProfit + :profit >= 0";
//    return updateUser(sql, params);
//  }

  public static boolean updatePointSkillAndPoint(long id, BigDecimal pointSkill, BigDecimal point) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("pointSkill", pointSkill);
    params.put("point", point);
    return updateUser(
      "pointSkill = pointSkill + :pointSkill, point = point + :point, pointUsed = pointUsed + :point * -1 where id = :id and pointSkill + :pointSkill >=0 and point + :point >= 0 ",
      params);
  }

  public static boolean updatePointAndXpUser(long id, BigDecimal point, BigDecimal xp) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("point", point);
    params.put("xp", xp);
    String sql = "";
    if (point.compareTo(BigDecimal.ZERO) > 0) {
      sql = "pointEarned = pointEarned + :point, ";
    }
    return updateUser(
      sql + "point = point + :point, xp = xp + :xp where id = :id and point + :point >=0 and xp + :xp >= 0", params);
  }

  public static long findRankByUserId(long userId) {
    return find(
      "select position from ( select id as id, row_number() over(order by miningPowerReal desc, createdAt asc) as position from User where id != 1) result where id =?1",
      userId).project(Long.class).firstResult();
  }

  public static long findRankEarnedByUserId(long userId) {
    return find(
      "select position from ( select id as id, row_number() over(order by pointEarned desc, miningPowerReal desc) as position from User where id != 1) result where id =?1",
      userId).project(Long.class).firstResult();
  }

  public static void updateRate(long id, BigDecimal rateMining, BigDecimal ratePurchase, BigDecimal rateReward,
    BigDecimal rateCountDown, BigDecimal rateCapacity) {
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("id", id);
      params.put("rateMining", rateMining);
      params.put("ratePurchase", ratePurchase);
      params.put("rateReward", rateReward);
      params.put("rateCountDown", rateCountDown);
      params.put("rateCapacity", rateCapacity);
      update(
        "rateMining = rateMining + :rateMining, ratePurchase = ratePurchase + :ratePurchase, " + " rateReward = rateReward + :rateReward,  rateCountDown = rateCountDown + :rateCountDown, rateCapacity = rateCapacity + :rateCapacity " + " where id= :id and rateMining >= 0 and rateReward >= 0 and rateCapacity >= 0 ",
        params);
    } catch (Exception e) {
      throw e;
    }
  }

  public static void updateLevelAndPointSkill(Long userId, Long levelNew, BigDecimal pointSkill) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("levelNew", levelNew);
    params.put("pointSkill", pointSkill);
    update(
      "level.id = :levelNew, pointSkill = pointSkill + :pointSkill, " + "maximumPower = maximumPower + (select sum(maxMiningPower) from Level where id > level.id and id <= :levelNew ) " + " where id = :userId and level.id < :levelNew and :pointSkill > 0 ",
      params);
  }

  public static void updateMiningPowerReal(long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    update(
      "update User set miningPowerReal = (select COALESCE(sum(ui.item.miningPower),0) from UserItem ui where ui.user.id = :userId and userDevice is not null ) * rateMining where id = :userId",
      params);
  }

  public static ResponsePage<FriendResponse> findFriendByUserAndPaging(PagingParameters pageable, long userId) {
    PanacheQuery<PanacheEntityBase> panacheQuery = find("ref.id =?1",
      Sort.descending("pointRef").and("createdAt", Sort.Direction.Ascending), userId);
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(FriendResponse.class).list(), pageable,
      panacheQuery.count());
  }

  /*public static ResponsePage<MemberLeagueResponse> findMemberLeagueByLeagueAndUserName(PagingParameters pageable,
    long leagueId, String username, long adminUserId) {
    String sql = "league.id = :leagueId and id != :adminUserId";
    Map<String, Object> params = new HashMap<>();
    params.put("leagueId", leagueId);
    params.put("adminUserId", adminUserId);
    if (StringUtils.isNotBlank(username)) {
      params.put("username", "%" + username.toLowerCase().trim() + "%");
      sql += " and lower(username) like :username";
    }
    PanacheQuery<PanacheEntityBase> panacheQuery = find(sql, pageable.getSort(), params);
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(MemberLeagueResponse.class).list(),
      pageable, panacheQuery.count());
  }*/

  public static long countFriendByUser(long userId) {
    return count("ref.id =?1", userId);
  }

  public static long countFriendEventByUser(long userId) {
    return count("ref.id =?1", userId);
  }
}

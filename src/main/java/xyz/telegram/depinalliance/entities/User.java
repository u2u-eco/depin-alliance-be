package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import org.apache.commons.lang3.RandomStringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;

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
  @Column(name = "username", updatable = false, nullable = false)
  public String username;
  @Column(name = "point", scale = 18, precision = 29)
  public BigDecimal point = BigDecimal.ZERO;
  @Column(name = "point_skill", scale = 18, precision = 29)
  public BigDecimal pointSkill = BigDecimal.ZERO;
  @Column(name = "point_un_claimed", scale = 18, precision = 29)
  public BigDecimal pointUnClaimed = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;
  @Column(name = "mining_power", scale = 18, precision = 29)
  public BigDecimal miningPower = BigDecimal.ZERO;
  @Column(name = "maximum_power", scale = 18, precision = 29)
  public BigDecimal maximumPower = BigDecimal.ZERO;
  @Column(name = "rate_mining", scale = 18, precision = 29)
  public BigDecimal rateMining = BigDecimal.ONE;
  @Column(name = "rate_purchase", scale = 18, precision = 29)
  public BigDecimal ratePurchase = BigDecimal.ONE;
  @Column(name = "rate_reward", scale = 18, precision = 29)
  public BigDecimal rateReward = BigDecimal.ONE;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ref_id")
  public User ref;
  @Column(name = "point_ref", scale = 18, precision = 29)
  public BigDecimal pointRef;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "level_id")
  public Level level;
  @Column(unique = true)
  public String code;
  @Column(name = "time_start_mining")
  public long timeStartMining = 0L;
  @Column(name = "last_login_time")
  public long lastLoginTime = 0L;
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
    return count("code", code);
  }

  public static int updateUser(String query, Map<String, Object> params) {
    return update(query, params);
  }

  public static int updatePointUser(long id, BigDecimal point) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("point", point);
    return updateUser("point = point + :point where id = :id and point + :point >=0", params);
  }
  public static boolean updatePointSkill(long id, BigDecimal pointSkill) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("pointSkill", pointSkill);
    return updateUser("pointSkill = pointSkill + :pointSkill where id = :id and pointSkill + :pointSkill >=0", params) == 1 ? true : false;
  }

  public static int updatePointAndXpUser(long id, BigDecimal point, BigDecimal xp) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("point", point);
    params.put("xp", xp);
    return updateUser("point = point + :point, xp = xp + :xp where id = :id and point + :point >=0 and xp + :xp >= 0",
      params);
  }

  public static long findRankByUserId(long userId) {
    return find(
      "select position from ( select id as id, row_number() over(order by miningPower desc, createdAt asc) as position from User) result where id =?1",
      userId).project(Long.class).firstResult();
  }

  public static boolean updateLevel(long id, long nextLevel, long maxLevel, BigDecimal pointUse, BigDecimal expUse) {
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("id", id);
      params.put("nextLevel", nextLevel);
      params.put("maxLevel", maxLevel);
      params.put("pointUse", pointUse);
      params.put("expUse", expUse);
      return update(
        "level.id = :nextLevel, point = point + :pointUse, xp = xp + :expUse " + "where id = :id and level.id < :nextLevel " + "and point + :pointUse >= 0 and xp + :expUse >= 0 and :nextLevel <= :maxLevel",
        params) == 1;
    } catch (Exception e) {
      throw e;
    }
  }

  public static void updateRate(long id, BigDecimal rateMining, BigDecimal ratePurchase, BigDecimal rateReward) {
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("id", id);
      params.put("rateMining", rateMining);
      params.put("ratePurchase", ratePurchase);
      params.put("rateReward", rateReward);
      update("rateMining = rateMining + :rateMining, ratePurchase = ratePurchase + :ratePurchase, " +
              "rateReward = rateReward + :rateReward where id= :id and :rateMining > 0 and :rateReward > 0", params);
    }catch (Exception e) {
      throw e;
    }
  }
//  public static void updateLevelByXp(long userId, BigDecimal xpAdded) {
//    try {
//      Map<String, Object> params = new HashMap<>();
//      params.put("userId", userId);
//      params.put("xpAdded", xpAdded);
//      update("level.id = sub.lvNew, pointSkill = pointSkill + sub.lvNew - level.id " +
//              "FROM (select u.xp, lv.id as lvNew from Level lv, User u " +
//              "where lv.expFrom <= u.xp + :xpAdded and u.xp + :xpAdded < lv.expTo) as sub " +
//              "where id = :userId and level.id < sub.lvNew and sub.lvNew - level.id > 0", params);
//    }catch (Exception e) {
//      throw e;
//    }
//  }
  public static void updateLevelAndPointSkill(Long userId, Long levelNew, BigDecimal pointSkill) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("levelNew", levelNew);
    params.put("pointSkill", pointSkill);
    update("level.id = :levelNew, pointSkill = pointSkill + :pointSkill" +
            "where id = :userId and level.id < :levelNew and :pointSkill > 0 ");
  }
}

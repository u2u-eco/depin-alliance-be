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
  @Column(name = "point_un_claimed", scale = 18, precision = 29)
  public BigDecimal pointUnClaimed = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;
  @Column(name = "mining_power", scale = 18, precision = 29)
  public BigDecimal miningPower = BigDecimal.ZERO;
  @Column(name = "maximum_power", scale = 18, precision = 29)
  public BigDecimal maximumPower = BigDecimal.ZERO;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ref_id")
  public User ref;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "level_id")
  public Level level;
  @Column(unique = true)
  public String code;
  @Column(name = "time_start_mining")
  public Long timeStartMining = 0L;
  @Column(name = "last_login_time")
  public Long lastLoginTime = 0L;
  @Column(name = "start_check_in")
  public Long startCheckIn = 0L;
  @Column(name = "end_check_in")
  public Long endCheckIn = 0L;
  @Column
  public String avatar;
  @Column
  public Enums.UserStatus status;

  public static User createUser(User user) {
    user.create();
    user.persist();
    user.code = RandomStringUtils.randomAlphanumeric(10);
    return user;
  }

  public static User findByCode(String code) {
    return find("code", code).firstResult();
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

}

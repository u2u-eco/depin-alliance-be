package xyz.telegram.depinalliance.entities;

import io.quarkus.panache.common.Parameters;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.models.response.UserSkillResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "user_skill")
public class UserSkill extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_id")
  public Skill skill;
  @Column(name = "level")
  public Long level = 0L;
  //    @Column(name = "rate_mining", scale = 18, precision = 29)
  //    public BigDecimal rateMining;
  //
  //    @Column(name = "power_mining", scale = 18, precision = 29)
  //    public BigDecimal powerMining;
  @Column(name = "level_upgrade")
  public Long levelUpgrade = 0L;
  @Column(name = "time_upgrade")
  public Long timeUpgrade = 0L;   //Milliseconds
  @Id
  @SequenceGenerator(name = "userSkillSequence", sequenceName = "user_skill_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSkillSequence")
  private Long id;

  public static void initUserSkill(User user, List<Skill> skills) {
    skills.forEach(sk -> {
      UserSkill userSkill = new UserSkill();
      userSkill.create();
      userSkill.user = user;
      userSkill.skill = sk;
      userSkill.persist();
    });
  }

  public static boolean updateLevel(Long userId, Long skillId, Long levelUpdate) {
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("userId", userId);
      params.put("skillId", skillId);
      params.put("levelUpdate", levelUpdate);
      return update("level= :levelUpdate " + "WHERE user.id = :userId AND skill.id= :skillId AND level < :levelUpdate",
        params) == 1 ? true : false;
    } catch (Exception e) {
      throw e;
    }
  }

  public static Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId) {
    try {
      return find("user.id = ?1 AND skill.id = ?2 ", userId, skillId).firstResultOptional();
    } catch (Exception e) {
      throw e;
    }
  }

  public static List<UserSkillResponse> findByUserId(long userId) {
    try {
      return find(
        "select s.id, s.name, us.level, s.maxLevel, us.timeUpgrade from " + UserSkill.class.getSimpleName() + " us inner join " + Skill.class.getSimpleName() + " s on us.skill.id = s.id " + "where us.user.id = :userId ",
        Parameters.with("userId", userId)).project(UserSkillResponse.class).list();
    } catch (Exception e) {
      throw e;
    }
  }

  //    public static List<UserSkillResponse> findByUserId (long userId) {
  //        try {
  //            return find("select s.id, s.name, us.level, s.maxLevel, us.timeUpgrade from "+Skill.class.getSimpleName()+" us left join "+UserSkill.class.getSimpleName()+" s on us.skill.id = s.id " +
  //                    "where us.user.id = :userId ", Parameters.with("userId", userId))
  //                    .project(UserSkillResponse.class).list();
  //        }catch (Exception e) {
  //            throw e;
  //        }
  //    }
  public static boolean upgradeSkillPending(long userId, long skillId, long timeUpgrade, long currentTime) {
    try {
      Map<String, Object> params = new HashMap<>();
      params.put("userId", userId);
      params.put("skillId", skillId);
      params.put("timeUpgrade", timeUpgrade);
      params.put("currentTime", currentTime);
      return update(
        "levelUpgrade = level + 1, timeUpgrade= :timeUpgrade " + "where user.id = :userId and skill.id = :skillId and timeUpgrade < :currentTime ",
        params) == 1 ? true : false;
    } catch (Exception e) {
      throw e;
    }
  }
}

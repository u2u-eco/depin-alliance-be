package xyz.telegram.depinalliance.entities;

import io.quarkus.panache.common.Parameters;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.models.response.UserSkillResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "user_skill")
public class UserSkill extends BaseEntity{
    @Id
    @SequenceGenerator(name = "userSkillSequence", sequenceName = "user_skill_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSkillSequence")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    public Skill skill;

    @Column(name = "level")
    public Long level;
    @Column(name = "rate_mining", scale = 18, precision = 29)
    public BigDecimal rateMining;

    @Column(name = "power_mining", scale = 18, precision = 29)
    public BigDecimal powerMining;
    @Column(name = "time_upgrade")
    public Long timeUpgrade = 0L;

    public void initUserSkill(User user, Skill skill, Long level, BigDecimal rateMining, BigDecimal powerMining) {
        this.user = user;
        this.skill = skill;
        this.level = level;
        this.rateMining = rateMining;
        this.powerMining = powerMining;
        persist();
    }
    public static void updateLevel(Long userId, Long skillId, Long maxLevel) {
        try {
            UserSkill.update("level=level+1 WHERE user.id = ?1 AND skill.id= ?2 AND level <= ?3",
                    userId, skillId, maxLevel);
        }catch (Exception e) {
            throw e;
        }
    }
    public static Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId) {
        try {
            return find("user.id = ?1 AND skill.id = ?2 ", userId, skillId).firstResultOptional();
        }catch (Exception e) {
            throw e;
        }
    }
    public static List<UserSkillResponse> findByUserId (long userId) {
        try {
            return find("select s.id, s.name, us.level, s.maxLevel, us.timeUpgrade from "+UserSkill.class.getSimpleName()+" us inner join "+Skill.class.getSimpleName()+" s on us.skill.id = s.id " +
                    "where us.user.id = :userId ", Parameters.with("userId", userId))
                    .project(UserSkillResponse.class).list();
        }catch (Exception e) {
            throw e;
        }
    }
}

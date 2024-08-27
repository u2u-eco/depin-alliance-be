package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
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
    public Integer level;

    @Column(name = "rate_mining", scale = 18, precision = 29)
    public BigDecimal rateMining;

    @Column(name = "power_mining", scale = 18, precision = 29)
    public BigDecimal powerMining;
    public void initUserSkill(User user, Skill skill, int level, BigDecimal rateMining, BigDecimal powerMining) {
        this.user = user;
        this.skill = skill;
        this.level = level;
        this.rateMining = rateMining;
        this.powerMining = powerMining;
        persist();
    }
    public static void updateLevel(Long userId, Long skillId, Integer maxLevel) {
        UserSkill.update("level=level+1 WHERE user_id = ?1 AND skill_id= ?2 AND level <= ?3",
                userId, skillId, maxLevel);
    }
    public static Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId) {
        return find("user_id = ?1 AND skill_id = ?2 ", userId, skillId).firstResultOptional();
    }
}

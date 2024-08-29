package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(name="skill_level")
public class SkillLevel extends PanacheEntityBase {
    @Id
    @SequenceGenerator(name = "skillLevelSequence", sequenceName = "skill_level_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skillLevelSequence")
    public Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="skill_id")
    public Skill skill;

    @Column(name = "level")
    public Long level;
    @Column(name = "fee_upgrade", scale = 18, precision = 29)
    public BigDecimal feeUpgrade;  //point skill
    @Column(name = "time_wait_upgrade")
    public Long timeWaitUpgrade; //seconds

    @Column(name = "rate_mining", scale = 18, precision = 29)
    public BigDecimal rateMining; //percent
    @Column(name = "rate_purchase", scale = 18, precision = 29)
    public BigDecimal ratePurchase; //percent
    @Column(name = "rate_reward", scale = 18, precision = 29)
    public BigDecimal rateReward; //percent

    public static Long getMaxLevel(Long skillId) {
        try {
            return find("select max(level) from SkillLevel where skill.id = :skillId ",
                    Parameters.with("skillId", skillId)).project(Long.class).firstResult();
        }catch (Exception e) {
            throw e;
        }
    }
    public static Optional<SkillLevel> findBySkillAndLevel(Long skillId, Long level) {
        try {
            return find("skill.id = ?1 and level = ?2 ", skillId, level)
                    .firstResultOptional();
        }catch (Exception e) {
            throw e;
        }
    }
}

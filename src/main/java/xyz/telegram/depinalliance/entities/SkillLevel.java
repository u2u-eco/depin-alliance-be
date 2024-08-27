package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
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
    @Column(name = "skill_id")
    public Long skillId;
    @Column(name = "level")
    public Long level;
    @Column(name = "fee_upgrade", scale = 18, precision = 29)
    public BigDecimal feePointUpgrade;  //point
    @Column(name = "time_wait_upgrade")
    public Long timeWaitUpgrade; //hours

    @Column(name = "rate_mining", scale = 18, precision = 29)
    public BigDecimal rateMining; //percent

    public static Integer getMaxLevel(Long skillId) {
        return find("select max(level) where id = :skillId ",
                Parameters.with("skillId", skillId))
                .project(Integer.class).firstResult();
    }
    public static Optional<SkillLevel> findBySkillAndLevel(Long skillId, Integer level) {
        return find("skill_id = ?1 and level = ?2 ", skillId, level)
                .firstResultOptional();
    }
}

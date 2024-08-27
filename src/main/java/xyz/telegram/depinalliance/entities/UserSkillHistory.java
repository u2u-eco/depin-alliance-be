package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_skill_history")
public class UserSkillHistory extends BaseEntity {
    @Id
    @SequenceGenerator(name = "userSkillHistorySequence", sequenceName = "user_skill_history_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSkillHistorySequence")
    public Long id;
    @Column(name = "type")
    public String type;
    @Column(name = "level_current")
    public Integer levelCurrent;
    @Column(name = "level_upgrade")
    public Integer levelUpgrade;
    @Column(name = "point_used", scale = 18, precision = 29)
    public BigDecimal pointUsed;
    @Column(name = "power_added", scale = 18, precision = 29)
    public BigDecimal powerAdded;
}

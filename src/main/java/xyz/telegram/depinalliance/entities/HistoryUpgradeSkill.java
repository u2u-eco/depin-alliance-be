package xyz.telegram.depinalliance.entities;

import io.quarkus.panache.common.Parameters;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Table(name = "history_upgrade_skill")
public class HistoryUpgradeSkill extends BaseEntity{
    @Id
    @SequenceGenerator(name = "historyUpgradeSkillSequence", sequenceName = "history_upgrade_skill_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "historyUpgradeSkillSequence")
    public Long id;
    @Column(name = "user_id")
    public long userId;
    @Column(name = "skill_id")
    public long skillId;
    @Column(name = "level_current")
    public long levelCurrent;
    @Column(name = "level_upgrade")
    public long levelUpgrade;
    @Column(name = "fee_upgrade", scale = 18, precision = 29)
    public BigDecimal feeUpgrade; //percent
    @Column(name = "time_wait_upgrade")
    public Long timeWaitUpgrade; //hours
    @Column(name = "rate_mining", scale = 18, precision = 29)
    public BigDecimal rateMining; //percent
    @Column(name = "rate_purchase", scale = 18, precision = 29)
    public BigDecimal ratePurchase; //percent
    @Column(name = "rate_reward", scale = 18, precision = 29)
    public BigDecimal rateReward; //percent
    @Column(name = "time_upgrade")
    public long timeUpgrade;
    public static void createHistory(HistoryUpgradeSkill history) {
        history.persist();
    }
}

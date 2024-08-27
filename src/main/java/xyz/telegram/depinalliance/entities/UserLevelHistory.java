package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_level_history")
public class UserLevelHistory extends BaseEntity{
    @Id
    @SequenceGenerator(name = "userLevelHistorySequence", sequenceName = "user_level_history_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userLevelHistorySequence")
    public Long id;
    @Column(name = "user_id")
    public Long userId;
    @Column(name = "level_current")
    public Long levelCurrent;
    @Column(name = "level_upgrade")
    public Long levelUpgrade;
    @Column(name = "point_used", scale = 18, precision = 29)
    public BigDecimal pointUsed;
    @Column(name = "exp_used", scale = 18, precision = 29)
    public BigDecimal expUsed;
    public static void createHistory(UserLevelHistory userLevelHistory) {
        userLevelHistory.persist();
    }
}

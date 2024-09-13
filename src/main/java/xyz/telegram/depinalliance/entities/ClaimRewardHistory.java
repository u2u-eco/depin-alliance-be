package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * @author holden on 13-Sep-2024
 */
@Entity
@Table(name = "claim_reward_histories")
public class ClaimRewardHistory extends BaseEntity {
  @Id
  @SequenceGenerator(name = "claimRewardHistorySequence", sequenceName = "claim_reward_history_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "claimRewardHistorySequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @Column(name = "claim_number")
  public long claimNumber;
  @Column(name = "point_claim", scale = 18, precision = 29)
  public BigDecimal pointClaim = BigDecimal.ZERO;
  @Column(name = "point_claimed", scale = 18, precision = 29)
  public BigDecimal pointClaimed = BigDecimal.ZERO;
  @Column(name = "point_bonus", scale = 18, precision = 29)
  public BigDecimal pointBonus = BigDecimal.ZERO;
  @Column(name = "rate_reward")
  public BigDecimal rateReward = BigDecimal.ZERO;
  @Column(name = "percent_bonus")
  public BigDecimal percentBonus = BigDecimal.ZERO;

}

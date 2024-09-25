package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * @author holden on 24-Sep-2024
 */
//@Entity
//@Table(name = "league_fund_histories")
public class LeagueFundHistory extends BaseEntity {
  @Id
  @SequenceGenerator(name = "leagueFundHistorySequence", sequenceName = "league_fund_history_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leagueFundHistorySequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true)
  public User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id", unique = true)
  public League league;
  @Column(name = "point", scale = 18, precision = 29, columnDefinition = "numeric(29, 18) DEFAULT 0")
  public BigDecimal point = BigDecimal.ZERO;
}

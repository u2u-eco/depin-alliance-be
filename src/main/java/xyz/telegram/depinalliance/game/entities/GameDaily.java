package xyz.telegram.depinalliance.game.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * @author holden on 04-Oct-2024
 */
@Entity
@Table(name = "game_daily", uniqueConstraints = { @UniqueConstraint(columnNames = { "userId", "date", "time" }) })
public class GameDaily extends BaseEntity {
  @Id
  @SequenceGenerator(name = "gameDailySequence", sequenceName = "game_daily_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gameDailySequence")
  public Long id;
  public Long userId;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agency_id")
  public GameItem agency;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tool_id")
  public GameItem tool;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "continent_id")
  public GameItem continent;
  public BigDecimal reward;
  @Column(name = "is_completed")
  public boolean isCompleted = false;
  public Long date;
  public long time;
}

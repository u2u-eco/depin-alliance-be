package xyz.telegram.depinalliance.game.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 04-Oct-2024
 */
@Entity
@Table(name = "game_results")
public class GameResult extends BaseEntity {
  @Id
  @SequenceGenerator(name = "gameResultSequence", sequenceName = "game_result_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gameResultSequence")
  public Long id;
  @Column
  public long userId;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_daily_id")
  public GameDaily gameDaily;
  public Enums.GameType type;
  public long gameId;
}

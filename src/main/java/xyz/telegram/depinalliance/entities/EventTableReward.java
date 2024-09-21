package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

import java.math.BigDecimal;

/**
 * @author holden on 17-Sep-2024
 */
@Entity
@Table(name = "event_table_rewards")
public class EventTableReward extends PanacheEntityBase {
  @Id
  @SequenceGenerator(name = "eventTableRewardSequence", sequenceName = "event_table_reward_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventTableRewardSequence")
  public Long id;
  @Column(name = "reward_table")
  public String rewardTable;
  @Column(name = "reward_type")
  public Enums.EventTableType rewardType;
  @Column(name = "amount", scale = 18, precision = 29)
  public BigDecimal amount;
  @Column(name = "amount_point", scale = 18, precision = 29)
  public BigDecimal amountPoint;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  public Item item;
  @Column(name = "from_rate")
  public long fromRate;
  @Column(name = "to_rate")
  public long toRate;
}

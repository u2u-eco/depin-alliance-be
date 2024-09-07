package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * @author holden on 07-Sep-2024
 */
@Entity
@Table(name = "event_box_point")
public class EventBoxPoint extends BaseEntity {
  @Id
  @SequenceGenerator(name = "eventBoxPointSequence", sequenceName = "event_box_point_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventBoxPointSequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  public Event event;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  public Item item;
  @Column(name = "index_box")
  public long indexBox;
  @Column(name = "point_required")
  public BigDecimal pointRequired;
  @Column(name = "reward_table")
  public String rewardTable;
}

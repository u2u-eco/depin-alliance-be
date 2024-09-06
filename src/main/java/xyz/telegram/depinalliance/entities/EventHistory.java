package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 06-Sep-2024
 */
@Entity
@Table(name = "event_histories")
public class EventHistory extends BaseEntity {
  @Id
  @SequenceGenerator(name = "eventHistorySequence", sequenceName = "event_history_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventHistorySequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  public Event event;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  public Enums.EventHistoryType type;
  @Column(name = "ref_id")
  public String refId;
}

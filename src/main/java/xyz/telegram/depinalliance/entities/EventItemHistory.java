package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

/**
 * @author holden on 07-Sep-2024
 */
@Entity
@Table(name = "event_item_histories")
public class EventItemHistory extends BaseEntity {
  @Id
  @SequenceGenerator(name = "eventItemHistorySequence", sequenceName = "event_item_history_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventItemHistorySequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  public Event event;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_item_id")
  public UserItem userItem;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_box_point_id")
  public EventBoxPoint eventBoxPoint;
  public String reward;
}

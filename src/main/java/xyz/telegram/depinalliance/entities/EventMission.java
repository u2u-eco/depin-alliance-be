package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

/**
 * @author holden on 06-Sep-2024
 */
@Entity
@Table(name = "event_missions")
public class EventMission extends BaseEntity {
  @Id
  @SequenceGenerator(name = "eventMissionSequence", sequenceName = "event_mission_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventMissionSequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mission_id")
  public Mission mission;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  public Event event;
}

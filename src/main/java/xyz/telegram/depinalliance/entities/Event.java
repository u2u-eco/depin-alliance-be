package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

/**
 * @author holden on 06-Sep-2024
 */
@Entity
@Table(name = "events")
public class Event extends BaseEntity {
  @Id
  @SequenceGenerator(name = "eventSequence", sequenceName = "event_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventSequence")
  public Long id;
  public String name;
  public String description;
  @Column(name = "is_active")
  public Boolean isActive;
  public String code;
}

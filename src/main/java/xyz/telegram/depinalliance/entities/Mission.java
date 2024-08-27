package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "missions")
public class Mission {
  @Id
  @SequenceGenerator(name = "missionSequence", sequenceName = "mission_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "missionSequence")
  public Long id;
//  public String group;
  public String name;
  public String description;
  public Enums.MissionType type;
  public String url;
}

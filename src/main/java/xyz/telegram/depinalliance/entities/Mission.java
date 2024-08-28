package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "missions")
public class Mission extends BaseEntity {
  @Id
  @SequenceGenerator(name = "missionSequence", sequenceName = "mission_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "missionSequence")
  public Long id;
  @Column(name = "group_mission")
  public String groupMission;
  public String name;
  public String description;
  public Enums.MissionType type;
  public String url;
  public int orders = 0;
  @Column(name = "is_fake")
  public boolean isFake = false;
  @Column(name = "point", scale = 18, precision = 29)
  public BigDecimal point = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;
}

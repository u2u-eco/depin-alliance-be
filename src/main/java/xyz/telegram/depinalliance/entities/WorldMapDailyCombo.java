package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 09-Oct-2024
 */
@Entity
@Table(name = "world_map_daily_combo")
public class WorldMapDailyCombo extends PanacheEntityBase {
  @Id
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agency_id")
  public WorldMapItem agency;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tool_id")
  public WorldMapItem tool;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "continent_id")
  public WorldMapItem continent;
  @Column(name = "mission_1_type")
  public Enums.WorldMapType mission1Type;
  @Column(name = "mission_1_title")
  public String mission1Title;
  @Column(name = "mission_1_detail", columnDefinition = "text")
  public String mission1Detail;
  @Column(name = "mission_2_type")
  public Enums.WorldMapType mission2Type;
  @Column(name = "mission_2_title")
  public String mission2Title;
  @Column(name = "mission_2_detail", columnDefinition = "text")
  public String mission2Detail;
  @Column(name = "mission_3_type")
  public Enums.WorldMapType mission3Type;
  @Column(name = "mission_3_title")
  public String mission3Title;
  @Column(name = "mission_3_detail", columnDefinition = "text")
  public String mission3Detail;
  @Column(name = "mission_4_type")
  public Enums.WorldMapType mission4Type;
  @Column(name = "mission_4_title")
  public String mission4Title;
  @Column(name = "mission_4_detail", columnDefinition = "text")
  public String mission4Detail;
  @Column(name = "mission_5_type")
  public Enums.WorldMapType mission5Type;
  @Column(name = "mission_5_title")
  public String mission5Title;
  @Column(name = "mission_5_detail", columnDefinition = "text")
  public String mission5Detail;
}

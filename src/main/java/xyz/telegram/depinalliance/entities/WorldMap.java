package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

import java.math.BigDecimal;

/**
 * @author holden on 04-Oct-2024
 */
@Entity
@Table(name = "world_map", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "date", "time" }) })
public class WorldMap extends BaseEntity {
  @Id
  @SequenceGenerator(name = "worldMapSequence", sequenceName = "world_map_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "worldMapSequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agency_id")
  public WorldMapItem agency;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tool_id")
  public WorldMapItem tool;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "continent_id")
  public WorldMapItem continent;
  @Column(name = "is_completed")
  public boolean isCompleted = false;
  public Long date;
  public long time;
  @Column(name = "next_time_play")
  public long nextTimePlay;
  @Column(name = "win_daily_combo", columnDefinition = "boolean false")
  public boolean winDailyCombo = false;
  @Column(name = "number_mission_completed")
  public int numberMissionCompleted = 0;

  @Column(name = "mission_1_type")
  public Enums.WorldMapType mission1Type;
  @Column(name = "mission_1_title")
  public String mission1Title;
  @Column(name = "mission_1_detail", columnDefinition = "text")
  public String mission1Detail;
  @Column(name = "mission_1_is_completed")
  public boolean mission1IsCompleted = false;
  @Column(name = "mission_1_location")
  public String mission1Location;
  @Column(name = "mission_1_location_name")
  public String mission1LocationName;
  @Column(name = "mission_1_created_at")
  public Long mission1CreatedAt;
  @Column(name = "mission_1_ended_at")
  public Long mission1EndedAt;
  @Column(name = "mission_1_reward")
  public BigDecimal mission1Reward;

  @Column(name = "mission_2_type")
  public Enums.WorldMapType mission2Type;
  @Column(name = "mission_2_title")
  public String mission2Title;
  @Column(name = "mission_2_detail", columnDefinition = "text")
  public String mission2Detail;
  @Column(name = "mission_2_is_completed")
  public boolean mission2IsCompleted = false;
  @Column(name = "mission_2_location")
  public String mission2Location;
  @Column(name = "mission_2_longitude")
  public String mission2Longitude;
  @Column(name = "mission_2_location_name")
  public String mission2LocationName;
  @Column(name = "mission_2_created_at")
  public Long mission2CreatedAt;
  @Column(name = "mission_2_ended_at")
  public Long mission2EndedAt;
  @Column(name = "mission_2_reward")
  public BigDecimal mission2Reward;

  @Column(name = "mission_3_type")
  public Enums.WorldMapType mission3Type;
  @Column(name = "mission_3_title")
  public String mission3Title;
  @Column(name = "mission_3_detail", columnDefinition = "text")
  public String mission3Detail;
  @Column(name = "mission_3_is_completed")
  public boolean mission3IsCompleted = false;
  @Column(name = "mission_3_location")
  public String mission3Location;
  @Column(name = "mission_3_longitude")
  public String mission3Longitude;
  @Column(name = "mission_3_location_name")
  public String mission3LocationName;
  @Column(name = "mission_3_created_at")
  public Long mission3CreatedAt;
  @Column(name = "mission_3_ended_at")
  public Long mission3EndedAt;
  @Column(name = "mission_3_reward")
  public BigDecimal mission3Reward;

  @Column(name = "mission_4_type")
  public Enums.WorldMapType mission4Type;
  @Column(name = "mission_4_title")
  public String mission4Title;
  @Column(name = "mission_4_detail", columnDefinition = "text")
  public String mission4Detail;
  @Column(name = "mission_4_is_completed")
  public boolean mission4IsCompleted = false;
  @Column(name = "mission_4_location")
  public String mission4Location;
  @Column(name = "mission_4_longitude")
  public String mission4Longitude;
  @Column(name = "mission_4_location_name")
  public String mission4LocationName;
  @Column(name = "mission_4_created_at")
  public Long mission4CreatedAt;
  @Column(name = "mission_4_ended_at")
  public Long mission4EndedAt;
  @Column(name = "mission_4_reward")
  public BigDecimal mission4Reward;

  @Column(name = "mission_5_type")
  public Enums.WorldMapType mission5Type;
  @Column(name = "mission_5_title")
  public String mission5Title;
  @Column(name = "mission_5_detail", columnDefinition = "text")
  public String mission5Detail;
  @Column(name = "mission_5_is_completed")
  public boolean mission5IsCompleted = false;
  @Column(name = "mission_5_location")
  public String mission5Location;
  @Column(name = "mission_5_longitude")
  public String mission5Longitude;
  @Column(name = "mission_5_location_name")
  public String mission5LocationName;
  @Column(name = "mission_5_created_at")
  public Long mission5CreatedAt;
  @Column(name = "mission_5_ended_at")
  public Long mission5EndedAt;
  @Column(name = "mission_5_reward")
  public BigDecimal mission5Reward;
}

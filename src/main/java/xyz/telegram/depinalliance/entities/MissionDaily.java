package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "mission_daily")
public class MissionDaily extends BaseEntity {
  @Id
  @SequenceGenerator(name = "missionSequence", sequenceName = "mission_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "missionSequence")
  public Long id;
  public String image;
  public String name;
  @Column(columnDefinition = "text")
  public String description;
  public Enums.MissionType type;
  public String url;
  public int orders = 0;
  @Column(name = "is_fake")
  public boolean isFake = true;
  @Column(name = "point", scale = 18, precision = 29)
  public BigDecimal point = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;
  @Column(name = "amount")
  public Long amount = 0L;
  @Column(name = "is_active")
  public boolean isActive = true;
  @Column(name = "refer_id")
  public String referId;
  @Column(name = "reward_type")
  public Enums.MissionRewardType rewardType;
  @Column(name = "reward_image")
  public String rewardImage;
  public Long date;

  public MissionDaily(Long id) {
    this.id = id;
  }

  public MissionDaily() {
  }

}

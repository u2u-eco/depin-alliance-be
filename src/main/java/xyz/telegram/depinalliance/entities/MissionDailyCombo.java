package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * @author holden on 09-Oct-2024
 */
@Entity
@Table(name = "mission_daily_combo")
public class MissionDailyCombo extends PanacheEntityBase {
  @Id
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agency_id")
  public MissionItem agency;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tool_id")
  public MissionItem tool;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "continent_id")
  public MissionItem continent;
  public BigDecimal point;
}

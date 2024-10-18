package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.math.BigDecimal;

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
}

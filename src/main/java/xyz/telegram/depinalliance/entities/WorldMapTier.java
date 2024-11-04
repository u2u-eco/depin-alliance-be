package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * @author holden on 17-Oct-2024
 */
@Entity
@Table(name = "world_map_tiers")
public class WorldMapTier extends PanacheEntityBase {
  @Id
  public Long id;
  @Column(name = "mining_power_from")
  public BigDecimal miningPowerFrom;
  @Column(name = "mining_power_to")
  public BigDecimal miningPowerTo;

  public static WorldMapTier findByMiningPowerFrom(BigDecimal miningPower) {
    return find("miningPowerFrom <= :miningPower and :miningPower < miningPowerTo",
      Parameters.with("miningPower", miningPower)).firstResult();
  }
}

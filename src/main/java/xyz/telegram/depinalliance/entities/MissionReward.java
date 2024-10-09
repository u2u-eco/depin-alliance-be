package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * @author holden on 09-Oct-2024
 */
@Entity
@Table(name = "mission_rewards")
public class MissionReward extends PanacheEntityBase {
  @Id
  public Long id;
  public BigDecimal point;
}

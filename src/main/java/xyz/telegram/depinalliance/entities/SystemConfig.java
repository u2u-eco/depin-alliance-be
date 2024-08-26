package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "system_configs")
public class SystemConfig extends PanacheEntityBase {
  @Id
  public Long id;
  public BigDecimal pointRef;

  public static SystemConfig find() {
    return findById(1);
  }
}

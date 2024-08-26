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
@Table(name = "daily_checkin")
public class DailyCheckin extends PanacheEntityBase {
  @Id
  public Long id;
  public String name;
  public BigDecimal point;
}

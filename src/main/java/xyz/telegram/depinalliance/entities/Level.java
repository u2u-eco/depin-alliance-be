package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "levels")
public class Level extends PanacheEntityBase {
  @Id
  public Long id;
  public String name;
  @Column(scale = 18, precision = 29)
  public BigDecimal point;
  @Column(scale = 18, precision = 29)
  public BigDecimal exp;

  public Level(Long id) {
    this.id = id;
  }

  public Level() {
  }
  public static Long maxLevel() {
    return find(" select max(id) from Level ").project(Long.class).firstResult();
  }
}

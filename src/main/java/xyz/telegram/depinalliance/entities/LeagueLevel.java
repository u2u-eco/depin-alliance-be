package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "league_levels")
public class LeagueLevel extends PanacheEntityBase {
  @Id
  public Long id;
  public String name;
  @Column(scale = 18, precision = 29)
  public BigDecimal expNextLevel;

  public static Long maxLevel() {
    return find(" select max(id) from Level ").project(Long.class).firstResult();
  }
}
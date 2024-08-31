package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
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
  @Column(name = "exp_from", scale = 18, precision = 29)
  public BigDecimal expFrom;
  @Column(name = "exp_to", scale = 18, precision = 29)
  public BigDecimal expTo;

  public LeagueLevel(Long id) {
    this.id = id;
  }

  public LeagueLevel() {

  }

  public static Long maxLevel() {
    return find("select max(id) from LeagueLevel").project(Long.class).firstResult();
  }

  public static LeagueLevel getLevelBeExp(BigDecimal exp) {
    return find(" expFrom <= :exp and :exp < expTo", Parameters.with("exp", exp)).firstResult();
  }
}

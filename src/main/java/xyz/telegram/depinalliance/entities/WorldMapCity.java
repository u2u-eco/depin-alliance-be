package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author holden on 16-Oct-2024
 */
@Entity
@Table(name = "world_map_cities")
public class WorldMapCity extends PanacheEntityBase {
  @Id
  public Long id;
  public String name;
  public String location;
  public String continent;
}

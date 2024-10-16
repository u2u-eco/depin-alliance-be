package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 04-Oct-2024
 */
@Entity
@Table(name = "world_map_items")
public class WorldMapItem extends PanacheEntityBase {
  @Id
  public Long id;
  public String name;
  public String code;
  public String description;
  public Enums.MissionItemType type;
  public String image;

  public WorldMapItem() {
  }

  public WorldMapItem(Long id) {
    this.id = id;
  }
}

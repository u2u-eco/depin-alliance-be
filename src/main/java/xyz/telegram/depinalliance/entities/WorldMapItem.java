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
  public Enums.WorldMapItemType type;
  public String image;

  public WorldMapItem() {
  }

  public WorldMapItem(Long id) {
    this.id = id;
  }

  public WorldMapItem(Long id, String name, String code, String description, Enums.WorldMapItemType type,
    String image) {
    this.id = id;
    this.name = name;
    this.code = code;
    this.description = description;
    this.type = type;
    this.image = image;
  }
}

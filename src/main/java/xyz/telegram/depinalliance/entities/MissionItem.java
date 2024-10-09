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
@Table(name = "mission_items")
public class MissionItem extends PanacheEntityBase {
  @Id
  public Long id;
  public String name;
  public String code;
  public String description;
  public Enums.MissionItemType type;
  public String image;

  public MissionItem() {
  }

  public MissionItem(Long id) {
    this.id = id;
  }
}

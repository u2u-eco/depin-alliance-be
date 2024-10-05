package xyz.telegram.depinalliance.game.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 04-Oct-2024
 */
@Entity
@Table(name = "game_items")
public class GameItem extends PanacheEntityBase {
  @Id
  public Long id;
  public String name;
  public String description;
  public Enums.GameItemType type;
}

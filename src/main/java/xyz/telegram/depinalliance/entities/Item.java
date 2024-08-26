package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "items")
public class Item extends BaseEntity {
  @Id
  @SequenceGenerator(name = "itemSequence", sequenceName = "item_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemSequence")
  public Long id;
  public String name;
  @Column(unique = true)
  public String code;
  public Enums.ItemType type;
  @Column(name = "mining_power", scale = 18, precision = 29)
  public BigDecimal miningPower = BigDecimal.ZERO;
  @Column(name = "price", scale = 18, precision = 29)
  public BigDecimal price = BigDecimal.ZERO;
  public String image;
  @Column(name = "point", scale = 18, precision = 29)
  public BigDecimal point = BigDecimal.ZERO;
}

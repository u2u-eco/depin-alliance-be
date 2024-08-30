package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.ItemResponse;
import xyz.telegram.depinalliance.common.models.response.ResponsePage;

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
  @Column
  public String name;
  @Column(unique = true)
  public String code;
  @Column
  public Enums.ItemType type;
  @Column(name = "mining_power", scale = 18, precision = 29)
  public BigDecimal miningPower = BigDecimal.ZERO;
  @Column(name = "price", scale = 18, precision = 29)
  public BigDecimal price = BigDecimal.ZERO;
  @Column
  public String image;

  public static Item findByCode(String code) {
    return find("code", code.toUpperCase()).firstResult();
  }

  public static ResponsePage<ItemResponse> findByTypeAndPaging(PagingParameters pageable, Enums.ItemType type) {
    PanacheQuery<PanacheEntityBase> panacheQuery = find("type = ?1", Sort.ascending("id"), type);
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(ItemResponse.class).list(), pageable,
      panacheQuery.count());
  }
}

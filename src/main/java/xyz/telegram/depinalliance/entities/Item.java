package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
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
  @Column(name = "is_can_buy", columnDefinition = "boolean default true")
  public boolean isCanBuy = true;
  @Column(name = "is_can_sell", columnDefinition = "boolean default true")
  public boolean isCanSell = true;

  public static Item findByCode(String code) {
    return find("code", code.toUpperCase()).firstResult();
  }

  public static ResponsePage<ItemResponse> findByTypeAndPaging(PagingParameters pageable, String type) {
    PanacheQuery<PanacheEntityBase> panacheQuery;
    if (StringUtils.isNotBlank(type)) {
      panacheQuery = find("type = ?1 and isCanBuy = true", pageable.getSort(),
        Enums.ItemType.valueOf(type.toUpperCase()));
    } else {
      panacheQuery = find("isCanBuy = true", pageable.getSort());
    }
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(ItemResponse.class).list(), pageable,
      panacheQuery.count());
  }
}

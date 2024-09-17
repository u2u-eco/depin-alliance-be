package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * @author holden on 05-Sep-2024
 */
@Entity
@Table(name = "user_item_trade_histories")
public class UserItemTradeHistory extends BaseEntity {
  @Id
  @SequenceGenerator(name = "userItemTradeHistorySequence", sequenceName = "user_item_trade_history_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userItemTradeHistorySequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_item_id")
  public UserItem userItem;
  @Column(name = "price_item", scale = 18, precision = 29)
  public BigDecimal priceItem;
  @Column(scale = 18, precision = 29)
  public BigDecimal price;
  @Column(name = "is_buy")
  public boolean isBuy;

  public UserItemTradeHistory(User user, UserItem userItem, BigDecimal priceItem, BigDecimal price, boolean isBuy) {
    this.user = user;
    this.userItem = userItem;
    this.priceItem = priceItem;
    this.price = price;
    this.isBuy = isBuy;
  }

  public UserItemTradeHistory() {
  }

  public static long countBuy(long userId) {
    return count("user.id = ?1 and isBuy = true", userId);
  }

  public UserItemTradeHistory create() {
    super.create();
    this.persist();
    return this;
  }
}

package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "user_items")
public class UserItem extends BaseEntity {
  @Id
  @SequenceGenerator(name = "userItemSequence", sequenceName = "user_item_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userItemSequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  public Item item;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_device_id")
  public UserDevice userDevice;

  public UserItem(User user, Item item, UserDevice userDevice) {
    this.user = user;
    this.item = item;
    this.userDevice = userDevice;
  }

  public UserItem() {
  }

  public static UserItem create(UserItem userItem) {
    userItem.create();
    userItem.persist();
    return userItem;
  }
}

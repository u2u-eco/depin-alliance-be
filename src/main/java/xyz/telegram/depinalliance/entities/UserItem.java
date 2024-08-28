package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.ResponsePage;
import xyz.telegram.depinalliance.common.models.response.UserItemResponse;

import java.util.List;
import java.util.Map;

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
  @Column(name = "is_sold", columnDefinition = "boolean default false")
  public boolean isSold = false;

  public UserItem(User user, Item item, UserDevice userDevice) {
    this.user = user;
    this.item = item;
    this.userDevice = userDevice;
  }

  public UserItem() {
  }

  public static List<UserItemResponse> findByUserId(long userId, Long deviceIndex) {
    if (deviceIndex == null) {
      return find("user.id = ?1 and userDevice is null and isSold = false", Sort.ascending("id"), userId).project(
        UserItemResponse.class).list();
    }
    return find("user.id = ?1 and userDevice.index = ?2 and isSold = false", Sort.ascending("id"), userId,
      deviceIndex).project(UserItemResponse.class).list();
  }

  public static ResponsePage<UserItemResponse> findByTypeAndPaging(PagingParameters pageable, Enums.ItemType type) {
    PanacheQuery<PanacheEntityBase> panacheQuery = find("item.type = ?1 and isSold = false", Sort.ascending("id"),
      type);
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(UserItemResponse.class).list(), pageable,
      panacheQuery.count());
  }

  public static int updateObject(String query, Map<String, Object> params) {
    return update(query, params);
  }

  public static UserItem findByIdAndUser(Long id, long userId) {
    return find("id = ?1 and user.id = ?2 and isSold = false", id, userId).firstResult();
  }

  public static UserItem create(UserItem userItem) {
    userItem.create();
    userItem.persist();
    return userItem;
  }

}

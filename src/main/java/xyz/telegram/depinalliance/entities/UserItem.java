package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.ResponsePage;
import xyz.telegram.depinalliance.common.models.response.UserItemResponse;

import java.util.HashMap;
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
  @Column(name = "is_active", columnDefinition = "boolean default false")
  public boolean isActive = true;

  public UserItem(User user, Item item, UserDevice userDevice) {
    this.user = user;
    this.item = item;
    this.userDevice = userDevice;
  }

  public UserItem() {
  }

  public static ResponsePage<UserItemResponse> findByUserIdAndType(PagingParameters pageable, long userId,
    String type) {
    String sqlSelect = "select i.name, i.code, i.type, i.miningPower, i.image, i.price, count(1), i.isCanSell, i.isCanOpen, i.isCanClaim ";
    String sqlFrom = " from UserItem ui inner join Item i on item.id = i.id ";
    String sqlWhere = " where ui.user.id = :userId and isActive = true ";
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    sqlWhere += " and userDevice is null";
    if (StringUtils.isNotBlank(type)) {
      sqlWhere += " and item.type = :type";
      params.put("type", Enums.ItemType.valueOf(type.toUpperCase()));
    }
    String sqlGroup = " group by i.name, i.code, i.type, i.miningPower, i.image, i.price, i.isCanSell, i.isCanOpen, i.isCanClaim";
    return new ResponsePage<>(
      find(sqlSelect + sqlFrom + sqlWhere + sqlGroup, pageable.getSort(), params).page(pageable.getPage())
        .project(UserItemResponse.class).list(), pageable,
      find("select count(distinct i.id) " + sqlFrom + sqlWhere, params).project(Long.class).firstResult());
  }

  public static ResponsePage<UserItemResponse> findByUserIdAndIndexAndType(PagingParameters pageable, long userId,
    Long index, String type) {
    String sqlSelect = "select ui.id, i.name, i.code, i.type, i.miningPower, i.image, i.price from UserItem ui inner join Item i on item.id = i.id ";
    String sqlWhere = " where ui.user.id = :userId and isActive = true";
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    if (StringUtils.isNotBlank(type)) {
      sqlWhere += " and item.type = :type";
      params.put("type", Enums.ItemType.valueOf(type.toUpperCase()));
    }
    if (index != null && index > 0) {
      sqlWhere += " and userDevice.index = :index";
      params.put("index", index);
    } else {
      sqlWhere += " and userDevice is null";
//      sqlSelect += " left join UserDevice ud on ud.id= ui.userDevice.id";
    }
    PanacheQuery<PanacheEntityBase> panacheQuery = find(sqlSelect + sqlWhere, pageable.getSort(), params);
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(UserItemResponse.class).list(), pageable,
      panacheQuery.count());
  }

  public static List<Long> findItemNotHasDevice(long userId, long itemId, int number) {
    String sql = "select ui.id from UserItem ui where userDevice is null and user.id = :userId and isActive = true and item.id = :itemId";
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("itemId", itemId);
    return find(sql, params).page(0, number).project(Long.class).list();
  }

  public static int updateObject(String query, Map<String, Object> params) {
    return update(query, params);
  }

  public static UserItem findByIdAndUser(Long id, long userId) {
    return find("id = ?1 and user.id = ?2 and isActive = true", id, userId).firstResult();
  }

  public static UserItem create(UserItem userItem) {
    userItem.create();
    userItem.persist();
    return userItem;
  }

}

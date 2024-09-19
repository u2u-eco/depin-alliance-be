package xyz.telegram.depinalliance.services;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.BoxUseKeyRequest;
import xyz.telegram.depinalliance.common.models.request.BuyItemRequest;
import xyz.telegram.depinalliance.common.models.request.ChangeNameDeviceRequest;
import xyz.telegram.depinalliance.common.models.request.SellItemRequest;
import xyz.telegram.depinalliance.common.models.response.ItemBoxOpenResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author holden on 26-Aug-2024
 */
@ApplicationScoped
public class DeviceService {

  @Inject
  RedisService redisService;
  @Inject
  UserService userService;

  @Transactional
  public boolean buyItem(User user, BuyItemRequest request) throws Exception {
    if (request == null || StringUtils.isBlank(request.code) || request.number < 1) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item item = redisService.findItemByCode(request.code);
    if (item == null) {
      throw new BusinessException(ResponseMessageConstants.DEVICE_ITEM_NOT_FOUND);
    }
    if (!item.isCanBuy) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    BigDecimal amount = item.price.multiply(new BigDecimal(request.number)).multiply(user.ratePurchase);
    if (user.point.compareTo(amount) < 0) {
      throw new BusinessException(ResponseMessageConstants.USER_POINT_NOT_ENOUGH);
    }
    if (request.index > 0) {
      UserDevice userDevice = UserDevice.findByUserAndIndex(user.id, request.index);
      if (userDevice == null) {
        throw new BusinessException(ResponseMessageConstants.DEVICE_USER_NOT_FOUND);
      }
      int maxSlot;
      int slotUsed;
      String query;
      switch (item.type) {
      case CPU:
        maxSlot = redisService.getSystemConfigInt(Enums.Config.CPU_SLOT);
        slotUsed = userDevice.slotCpuUsed;
        query = "slotCpuUsed = slotCpuUsed + :item";
        break;
      case GPU:
        maxSlot = redisService.getSystemConfigInt(Enums.Config.GPU_SLOT);
        slotUsed = userDevice.slotGpuUsed;
        query = "slotGpuUsed = slotGpuUsed + :item";
        break;
      case RAM:
        maxSlot = redisService.getSystemConfigInt(Enums.Config.RAM_SLOT);
        slotUsed = userDevice.slotRamUsed;
        query = "slotRamUsed = slotRamUsed + :item";
        break;
      case STORAGE:
        maxSlot = redisService.getSystemConfigInt(Enums.Config.STORAGE_SLOT);
        slotUsed = userDevice.slotStorageUsed;
        query = "slotStorageUsed = slotStorageUsed + :item";
        break;
      default:
        throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
      }
      if (slotUsed + request.number > maxSlot) {
        throw new BusinessException(ResponseMessageConstants.DEVICE_USER_CANNOT_ADD_MORE_ITEM);
      }
      for (int i = 0; i < request.number; i++) {
        UserItem userItem = new UserItem(user, item, userDevice);
        UserItem.create(userItem);
        new UserItemTradeHistory(user, userItem, item.price, item.price.multiply(user.ratePurchase), true).create();
      }
      Map<String, Object> params = new HashMap<>();
      params.put("id", userDevice.id);
      params.put("item", request.number);
      params.put("miningPower", item.miningPower);
      UserDevice.updateObject(query + ", totalMiningPower = totalMiningPower + :miningPower where id = :id", params);
      BigDecimal miningPower = item.miningPower.multiply(new BigDecimal(request.number));
      userService.changeMiningPower(user, miningPower);
    } else {
      for (int i = 0; i < request.number; i++) {
        UserItem userItem = new UserItem(user, item, null);
        UserItem.create(userItem);
        new UserItemTradeHistory(user, userItem, item.price, item.price.multiply(user.ratePurchase), true).create();
      }
    }
    User.updatePointUser(user.id, amount.multiply(new BigDecimal("-1")));
    return true;
  }

  @Transactional
  public boolean addItem(User user, int index, long itemId) throws Exception {
    UserDevice userDevice = UserDevice.findByUserAndIndex(user.id, index);
    if (userDevice == null) {
      throw new BusinessException(ResponseMessageConstants.DEVICE_USER_NOT_FOUND);
    }
    UserItem userItem = UserItem.findByIdAndUser(itemId, user.id);
    if (userItem == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (userItem.userDevice != null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    int maxSlot;
    int slotUsed;
    Item item = redisService.findItemById(userItem.item.id);
    String query = "{field} = {field} + 1, totalMiningPower = totalMiningPower + :miningPower where id =:id and {field} + 1 <= :maxSlot";
    switch (item.type) {
    case CPU:
      maxSlot = redisService.getSystemConfigInt(Enums.Config.CPU_SLOT);
      slotUsed = userDevice.slotCpuUsed;
      query = query.replace("{field}", "slotCpuUsed");
      break;
    case GPU:
      maxSlot = redisService.getSystemConfigInt(Enums.Config.GPU_SLOT);
      slotUsed = userDevice.slotGpuUsed;
      query = query.replace("{field}", "slotGpuUsed");
      break;
    case RAM:
      maxSlot = redisService.getSystemConfigInt(Enums.Config.RAM_SLOT);
      slotUsed = userDevice.slotRamUsed;
      query = query.replace("{field}", "slotRamUsed");
      break;
    case STORAGE:
      maxSlot = redisService.getSystemConfigInt(Enums.Config.STORAGE_SLOT);
      slotUsed = userDevice.slotStorageUsed;
      query = query.replace("{field}", "slotStorageUsed");
      break;
    default:
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (slotUsed + 1 > maxSlot) {
      throw new BusinessException(ResponseMessageConstants.DEVICE_USER_CANNOT_ADD_MORE_ITEM);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("id", userDevice.id);
    params.put("maxSlot", maxSlot);
    params.put("miningPower", item.miningPower);
    if (!UserDevice.updateObject(query, params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("userDeviceId", userDevice.id);
    UserItem.updateObject(" userDevice.id = :userDeviceId where id = :id and userDevice is null", paramsUserItem);
    userService.changeMiningPower(user, item.miningPower);
    return true;
  }

  @Transactional
  public boolean removeItem(User user, long itemId) throws Exception {
    UserItem userItem = UserItem.findByIdAndUser(itemId, user.id);
    if (userItem == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (userItem.userDevice == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    String query = "{field} = {field} - 1, totalMiningPower = totalMiningPower - :miningPower where id =:id and {field} - 1 >=0 ";
    Item item = redisService.findItemById(userItem.item.id);
    switch (item.type) {
    case CPU:
      query = query.replace("{field}", "slotCpuUsed");
      break;
    case GPU:
      query = query.replace("{field}", "slotGpuUsed");
      break;
    case RAM:
      query = query.replace("{field}", "slotRamUsed");
      break;
    case STORAGE:
      query = query.replace("{field}", "slotStorageUsed");
      break;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("id", userItem.userDevice.id);
    params.put("miningPower", item.miningPower);
    if (!UserDevice.updateObject(query, params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }

    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("userDeviceId", null);
    UserItem.updateObject(" userDevice.id = :userDeviceId where id = :id and userDevice is not null", paramsUserItem);
    userService.changeMiningPower(user, item.miningPower.multiply(new BigDecimal("-1")));
    return true;
  }

  @Transactional
  public boolean sellItem(User user, long itemId) throws Exception {
    UserItem userItem = UserItem.findByIdAndUser(itemId, user.id);
    if (userItem == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (userItem.userDevice != null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item item = redisService.findItemById(userItem.item.id);
    if (!item.isCanSell) {
      throw new BusinessException(ResponseMessageConstants.ITEM_CANNOT_SELL);
    }
    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("isActive", false);
    if (UserItem.updateObject(" isActive = :isActive where id = :id and isActive = true", paramsUserItem) <= 0) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    User.updatePointUser(user.id, item.price.multiply(new BigDecimal("0.5")));
    new UserItemTradeHistory(user, userItem, item.price, item.price.multiply(new BigDecimal("0.5")),
      false).create();
    return true;
  }

  @Transactional
  public boolean sellItem(User user, SellItemRequest request) throws Exception {
    if (request == null || StringUtils.isBlank(request.code) || request.number <= 0) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item item = redisService.findItemByCode(request.code);
    if (item == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    if (!item.isCanSell) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    List<Long> itemIds = UserItem.findItemNotHasDevice(user.id, item.id, request.number);
    if (itemIds.isEmpty() || itemIds.size() < request.number) {
      throw new BusinessException(ResponseMessageConstants.ITEM_SELL_NOT_ENOUGH);
    }

    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("ids", itemIds);
    paramsUserItem.put("isActive", false);
    if (UserItem.updateObject(" isActive = :isActive where id in (:ids) and isActive = true",
      paramsUserItem) < request.number) {
      throw new BusinessException(ResponseMessageConstants.ITEM_SELL_NOT_ENOUGH);
    }
    for (Long id : itemIds) {
      UserItem userItem = UserItem.findById(id);
      new UserItemTradeHistory(user, userItem, item.price, item.price.multiply(new BigDecimal("0.5")),
        false).create();
    }
    User.updatePointUser(user.id, item.price.multiply(new BigDecimal(request.number)).multiply(new BigDecimal("0.5")));
    return true;
  }

  @Transactional
  public boolean swapItem(User user, long itemRemoveId, long itemAddId) throws Exception {
    UserItem userItemRemove = UserItem.findByIdAndUser(itemRemoveId, user.id);
    if (userItemRemove == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (userItemRemove.userDevice == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    UserItem userItemAdd = UserItem.findByIdAndUser(itemAddId, user.id);
    if (userItemAdd == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (userItemAdd.userDevice != null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item removeItem = redisService.findItemById(itemRemoveId);
    Item addItem = redisService.findItemById(itemAddId);
    String query = "totalMiningPower = totalMiningPower - :miningPowerOld + :miningPowerNew where id =:id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", userItemRemove.userDevice.id);
    params.put("miningPowerOld", removeItem.miningPower);
    params.put("miningPowerNew", addItem.miningPower);
    if (!UserDevice.updateObject(query, params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    Map<String, Object> paramsUserItemAdd = new HashMap<>();
    paramsUserItemAdd.put("id", itemAddId);
    paramsUserItemAdd.put("userDeviceId", userItemAdd.userDevice.id);
    if (UserItem.updateObject(" userDevice.id = :userDeviceId where id = :id and userDevice is null",
      paramsUserItemAdd) == 0) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }

    Map<String, Object> paramsUserItemRemove = new HashMap<>();
    paramsUserItemRemove.put("id", itemRemoveId);
    paramsUserItemRemove.put("userDeviceId", null);
    if (UserItem.updateObject(" userDevice.id = :userDeviceId where id = :id and userDevice is not null",
      paramsUserItemRemove) == 0) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    userService.changeMiningPower(user, removeItem.miningPower, addItem.miningPower);
    return true;
  }

  @Transactional
  public boolean changeNameDevice(User user, ChangeNameDeviceRequest request) throws Exception {
    if (request == null || request.index <= 0 || StringUtils.isBlank(request.name) || request.name.trim()
      .length() > 40) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    UserDevice userDevice = UserDevice.findByUserAndIndex(user.id, request.index);
    if (userDevice == null) {
      throw new BusinessException(ResponseMessageConstants.DEVICE_USER_NOT_FOUND);
    }
    Map<String, Object> paramsDevice = new HashMap<>();
    paramsDevice.put("id", userDevice.id);
    paramsDevice.put("name", request.name.trim());
    UserDevice.updateObject(" name = :name where id = :id", paramsDevice);
    return true;
  }

  @Transactional
  public int addDevice(User user) {
    long maxDevice = userService.maxDeviceUserByLevel(user.level.id);
    long numberDeviceUser = UserDevice.count("user.id = ?1", user.id);
    if (numberDeviceUser >= maxDevice) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    BigDecimal pointBuy = new BigDecimal(
      Objects.requireNonNull(redisService.findConfigByKey(Enums.Config.POINT_BUY_DEVICE)));
    if (pointBuy.compareTo(BigDecimal.ZERO) > 0) {
      if (user.point.compareTo(pointBuy) < 0) {
        throw new BusinessException(ResponseMessageConstants.USER_POINT_NOT_ENOUGH);
      }
      User.updatePointUser(user.id, pointBuy.multiply(new BigDecimal("-1")));
    }
    UserDevice userDevice = new UserDevice();
    userDevice.user = user;
    userDevice.name = "Device " + (numberDeviceUser + 1);
    userDevice.index = (int) (numberDeviceUser + 1);
    UserDevice.create(userDevice);
    Map<String, Object> params = new HashMap<>();
    params.put("id", user.id);
    params.put("maxDevice", maxDevice);
    if (!User.updateUser("totalDevice = totalDevice + 1 where id = :id and totalDevice + 1 <= :maxDevice", params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    return userDevice.index;
  }

  public BigDecimal estimateUseKeyBox(User user, BoxUseKeyRequest request) {
    if (request == null || StringUtils.isBlank(request.code) || request.amount <= 0) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item item = redisService.findItemByCode(request.code);
    if (item == null || !item.isCanOpen) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    long countInactive = UserItem.count("item.id = ?1 and user.id = ?2 and isActive = false", item.id, user.id);
    return EventBoxPoint.find(
      "select sum(pointRequired) from EventBoxPoint where item.id = ?1 and indexBox > ?2 and indexBox<= ?3", item.id,
      countInactive, (countInactive + request.amount)).project(BigDecimal.class).firstResult();
  }

  @Transactional
  public List<ItemBoxOpenResponse> useKeyBox(User user, BoxUseKeyRequest request) {
    if (request == null || StringUtils.isBlank(request.code) || request.amount <= 0) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item item = redisService.findItemByCode(request.code);
    if (item == null || !item.isCanOpen) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    List<Long> itemIds = UserItem.findItemNotHasDevice(user.id, item.id, request.amount);
    if (itemIds.size() != request.amount) {
      throw new BusinessException(ResponseMessageConstants.ITEM_OPEN_NOT_ENOUGH);
    }
    long countInactive = UserItem.count("item.id = ?1 and user.id = ?2 and isActive = false", item.id, user.id);
    BigDecimal pointRequire = EventBoxPoint.find(
      "select sum(pointRequired) from EventBoxPoint where item.id = ?1 and indexBox > ?2 and indexBox<= ?3", item.id,
      countInactive, (countInactive + request.amount)).project(BigDecimal.class).firstResult();
    if (pointRequire.compareTo(user.point) > 0) {
      throw new BusinessException(ResponseMessageConstants.USER_POINT_NOT_ENOUGH);
    }
    if (!User.updatePointUser(user.id, pointRequire.multiply(new BigDecimal("-1")))) {
      throw new BusinessException(ResponseMessageConstants.USER_POINT_NOT_ENOUGH);
    }
    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("ids", itemIds);
    paramsUserItem.put("isActive", false);
    if (UserItem.updateObject(" isActive = :isActive where id in (:ids) and isActive = true",
      paramsUserItem) < request.amount) {
      throw new BusinessException(ResponseMessageConstants.ITEM_OPEN_NOT_ENOUGH);
    }
    List<EventBoxPoint> eventBoxPoints = EventBoxPoint.list("item.id = ?1 and indexBox > ?2 and indexBox<= ?3 ",
      Sort.ascending("indexBox"), item.id, countInactive, (countInactive + request.amount));
    List<ItemBoxOpenResponse> rs = new ArrayList<>();
    for (int i = 0; i < eventBoxPoints.size(); i++) {
      UserItem userItem = new UserItem();
      userItem.id = itemIds.get(i);
      EventBoxPoint eventBoxPoint = eventBoxPoints.get(i);
      EventItemHistory eventItemHistory = new EventItemHistory();
      eventItemHistory.create();
      eventItemHistory.eventBoxPoint = eventBoxPoint;
      eventItemHistory.userItem = userItem;
      ItemBoxOpenResponse response = eventTable(user, eventBoxPoint.rewardTable);
      eventItemHistory.reward = response.toString();
      eventItemHistory.event = new Event(1L);
      eventItemHistory.persist();
      rs.add(response);
    }
    return rs;
  }

  public ItemBoxOpenResponse eventTable(User user, String rewardTable) {
    int a = new Random().nextInt(1000);
    EventTableReward eventTableReward = EventTableReward.find("rewardTable = ?1 and fromRate <= ?2 and toRate > ?3",
      rewardTable, a, a).firstResult();
    if (eventTableReward != null) {
      switch (eventTableReward.rewardType) {
      case POINT:
        if (!User.updatePointUser(user.id, eventTableReward.amount)) {
          throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
        }
        return new ItemBoxOpenResponse("Point", Utils.stripDecimalZeros(eventTableReward.amount).toString(),
          Utils.stripDecimalZeros(eventTableReward.amount));
      case DEVICE:
        UserItem.create(new UserItem(user, eventTableReward.item, null));
        return new ItemBoxOpenResponse(eventTableReward.item.type.name(), eventTableReward.item.name,
          Utils.stripDecimalZeros(eventTableReward.item.price));
      case USDT:
        if (Event.updateTotalUsdt(eventTableReward.amount, 1L)) {
          UserItem.create(new UserItem(user, eventTableReward.item, null));
          return new ItemBoxOpenResponse("USDT", Utils.stripDecimalZeros(eventTableReward.amount).toString(),
            Utils.stripDecimalZeros(eventTableReward.amount));
        } else {
          if (!User.updatePointUser(user.id, eventTableReward.amountPoint)) {
            throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
          }
          return new ItemBoxOpenResponse("Point", Utils.stripDecimalZeros(eventTableReward.amountPoint).toString(),
            Utils.stripDecimalZeros(eventTableReward.amountPoint));
        }
      }
    }
    return null;
  }

}

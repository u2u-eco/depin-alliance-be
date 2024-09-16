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
  SystemConfigService systemConfigService;
  @Inject
  UserService userService;

  @Transactional
  public boolean buyItem(User user, BuyItemRequest request) throws Exception {
    if (request == null || StringUtils.isBlank(request.code) || request.number < 1) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item item = Item.findByCode(request.code);
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
      int maxSlot = 0;
      int slotUsed = 0;
      String query = "";
      switch (item.type) {
      case CPU:
        maxSlot = systemConfigService.getSystemConfigInt(Enums.Config.CPU_SLOT);
        slotUsed = userDevice.slotCpuUsed;
        query = "slotCpuUsed = slotCpuUsed + :item";
        break;
      case GPU:
        maxSlot = systemConfigService.getSystemConfigInt(Enums.Config.GPU_SLOT);
        slotUsed = userDevice.slotGpuUsed;
        query = "slotGpuUsed = slotGpuUsed + :item";
        break;
      case RAM:
        maxSlot = systemConfigService.getSystemConfigInt(Enums.Config.RAM_SLOT);
        slotUsed = userDevice.slotRamUsed;
        query = "slotRamUsed = slotRamUsed + :item";
        break;
      case STORAGE:
        maxSlot = systemConfigService.getSystemConfigInt(Enums.Config.STORAGE_SLOT);
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
    String query = "{field} = {field} + 1, totalMiningPower = totalMiningPower + :miningPower where id =:id and {field} + 1 <= :maxSlot";
    switch (userItem.item.type) {
    case CPU:
      maxSlot = systemConfigService.getSystemConfigInt(Enums.Config.CPU_SLOT);
      slotUsed = userDevice.slotCpuUsed;
      query = query.replace("{field}", "slotCpuUsed");
      break;
    case GPU:
      maxSlot = systemConfigService.getSystemConfigInt(Enums.Config.GPU_SLOT);
      slotUsed = userDevice.slotGpuUsed;
      query = query.replace("{field}", "slotGpuUsed");
      break;
    case RAM:
      maxSlot = systemConfigService.getSystemConfigInt(Enums.Config.RAM_SLOT);
      slotUsed = userDevice.slotRamUsed;
      query = query.replace("{field}", "slotRamUsed");
      break;
    case STORAGE:
      maxSlot = systemConfigService.getSystemConfigInt(Enums.Config.STORAGE_SLOT);
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
    params.put("miningPower", userItem.item.miningPower);
    if (!UserDevice.updateObject(query, params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("userDeviceId", userDevice.id);
    UserItem.updateObject(" userDevice.id = :userDeviceId where id = :id and userDevice is null", paramsUserItem);
    userService.changeMiningPower(user, userItem.item.miningPower);
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
    switch (userItem.item.type) {
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
    params.put("miningPower", userItem.item.miningPower);
    if (!UserDevice.updateObject(query, params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }

    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("userDeviceId", null);
    UserItem.updateObject(" userDevice.id = :userDeviceId where id = :id and userDevice is not null", paramsUserItem);
    userService.changeMiningPower(user, userItem.item.miningPower.multiply(new BigDecimal("-1")));
    return true;
  }

  @Transactional
  public boolean sellItem(User user, long itemId) throws Exception {
    UserItem userItem = UserItem.findByIdAndUser(itemId, user.id);
    if (userItem == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (userItem.userDevice != null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    } else if (!userItem.item.isCanSell) {
      throw new BusinessException(ResponseMessageConstants.ITEM_CANNOT_SELL);
    }
    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("isActive", false);
    if (UserItem.updateObject(" isActive = :isActive where id = :id and isActive = true", paramsUserItem) <= 0) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    User.updatePointUser(user.id, userItem.item.price.multiply(new BigDecimal("0.5")));
    new UserItemTradeHistory(user, userItem, userItem.item.price, userItem.item.price.multiply(new BigDecimal("0.5")),
      false).create();
    return true;
  }

  @Transactional
  public boolean sellItem(User user, SellItemRequest request) throws Exception {
    if (request == null || StringUtils.isBlank(request.code) || request.number <= 0) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item item = Item.findByCode(request.code);
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
      new UserItemTradeHistory(user, userItem, userItem.item.price, userItem.item.price.multiply(new BigDecimal("0.5")),
        false).create();
    }
    User.updatePointUser(user.id, item.price.multiply(new BigDecimal(request.number)).multiply(new BigDecimal("0.5")));
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
    BigDecimal pointBuy = new BigDecimal(Objects.requireNonNull(systemConfigService.findByKey(Enums.Config.POINT_BUY_DEVICE)));
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
    Item item = Item.findByCode(request.code);
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
    Item item = Item.findByCode(request.code);
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
      ItemBoxOpenResponse response = null;
      if (eventBoxPoint.rewardTable.contains("1")) {
        response = eventTable1(user);
      } else if (eventBoxPoint.rewardTable.contains("2")) {
        response = eventTable2(user);
      } else if (eventBoxPoint.rewardTable.contains("3")) {
        response = eventTable3(user);
      }

      eventItemHistory.reward = response.toString();
      eventItemHistory.persist();
      rs.add(response);
    }
    return rs;
  }

  public ItemBoxOpenResponse eventTable1(User user) {
    BigDecimal amount;
    int a = new Random().nextInt(100);
    if (a < 20) {
      amount = new BigDecimal("7000");
    } else if (a < 40) {
      amount = new BigDecimal("8092");
    } else if (a < 60) {
      amount = new BigDecimal("9354");
    } else if (a < 80) {
      amount = new BigDecimal("10814");
    } else {
      amount = new BigDecimal("12501");
    }
    if (!User.updatePointUser(user.id, amount)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    return new ItemBoxOpenResponse("Point", Utils.stripDecimalZeros(amount).toString());
  }

  public ItemBoxOpenResponse eventTable2(User user) {
    Item item;
    int a = new Random().nextInt(1000);
    ItemBoxOpenResponse itemBoxOpenResponse = null;
    if (a < 300) {
      item = Item.findByCode(Enums.ItemSpecial.USDT_0_001.name());
      itemBoxOpenResponse = new ItemBoxOpenResponse("USDT", "0.001");
    } else if (a < 325) {
      item = Item.findByCode(Enums.ItemSpecial.USDT_0_002.name());
      itemBoxOpenResponse = new ItemBoxOpenResponse("USDT", "0.002");
    } else if (a < 525) {
      item = Item.findByCode("SSD_128GB");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 725) {
      item = Item.findByCode("RAM_16GB");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 875) {
      item = Item.findByCode("CPU_DYSEN_5_8000_SERIES");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 925) {
      item = Item.findByCode("CPU_DOCK_T5_15TH");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 975) {
      item = Item.findByCode("DEFORCE_MX450");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else {
      item = Item.findByCode("CPU_DOCK_T7_4TH");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    }
    if (item != null) {
      UserItem userItem = new UserItem(user, item, null);
      UserItem.create(userItem);
    }
    return itemBoxOpenResponse;
  }

  public ItemBoxOpenResponse eventTable3(User user) {
    Item item = null;
    BigDecimal amount = BigDecimal.ZERO;
    int a = new Random().nextInt(100);
    ItemBoxOpenResponse itemBoxOpenResponse = null;
    if (a < 8) {
      item = Item.findByCode(Enums.ItemSpecial.USDT_0_001.name());
      itemBoxOpenResponse = new ItemBoxOpenResponse("USDT", "0.001");
    } else if (a < 10) {
      item = Item.findByCode(Enums.ItemSpecial.USDT_0_002.name());
      itemBoxOpenResponse = new ItemBoxOpenResponse("USDT", "0.002");
    } else if (a < 12) {
      item = Item.findByCode(Enums.ItemSpecial.USDT_0_1.name());
      itemBoxOpenResponse = new ItemBoxOpenResponse("USDT", "0.1");
    } else if (a < 14) {
      item = Item.findByCode(Enums.ItemSpecial.USDT_0_5.name());
      itemBoxOpenResponse = new ItemBoxOpenResponse("USDT", "0.5");
    } else if (a < 16) {
      item = Item.findByCode(Enums.ItemSpecial.USDT_1.name());
      itemBoxOpenResponse = new ItemBoxOpenResponse("USDT", "1");
    } else if (a < 36) {
      item = Item.findByCode("SSD_4TB");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 52) {
      item = Item.findByCode("CPU_DYSEN_7_4000_SERIES");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 64) {
      item = Item.findByCode("CPU_DOCK_T7_6TH");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 66) {
      item = Item.findByCode("DEFORCE_RTX_2060_12GB");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 68) {
      item = Item.findByCode("CPU_DYSEN_7_6000_SERIES");
      itemBoxOpenResponse = new ItemBoxOpenResponse(item.type.name(), item.name);
    } else if (a < 80) {
      amount = new BigDecimal(35000);
    } else if (a < 92) {
      amount = new BigDecimal(75000);
    } else if (a < 96) {
      amount = new BigDecimal(125000);
    } else {
      amount = new BigDecimal(175000);
    }
    if (item != null) {
      UserItem userItem = new UserItem(user, item, null);
      UserItem.create(userItem);
    } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
      if (!User.updatePointUser(user.id, amount)) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
      return new ItemBoxOpenResponse("Point", Utils.stripDecimalZeros(amount).toString());
    }
    return itemBoxOpenResponse;
  }
}

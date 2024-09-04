package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.BuyItemRequest;
import xyz.telegram.depinalliance.common.models.request.ChangeNameDeviceRequest;
import xyz.telegram.depinalliance.common.models.request.SellItemRequest;
import xyz.telegram.depinalliance.entities.Item;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.entities.UserDevice;
import xyz.telegram.depinalliance.entities.UserItem;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    BigDecimal amount = item.price.multiply(new BigDecimal(request.number));
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
      }
      if (slotUsed + request.number > maxSlot) {
        throw new BusinessException(ResponseMessageConstants.DEVICE_USER_CANNOT_ADD_MORE_ITEM);
      }
      for (int i = 0; i < request.number; i++) {
        UserItem.create(new UserItem(user, item, userDevice));
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
        UserItem.create(new UserItem(user, item, null));
      }
    }
    User.updatePointUser(user.id, amount.multiply(user.ratePurchase).multiply(new BigDecimal("-1")));
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
    int maxSlot = 0;
    int slotUsed = 0;
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
    }
    if (slotUsed + 1 > maxSlot) {
      throw new BusinessException(ResponseMessageConstants.DEVICE_USER_CANNOT_ADD_MORE_ITEM);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("id", userDevice.id);
    params.put("maxSlot", maxSlot);
    params.put("miningPower", userItem.item.miningPower);
    if (UserDevice.updateObject(query, params) <= 0) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("userDeviceId", userDevice.id);
    UserItem.updateObject(" userDevice.id = :userDeviceId where id = :id", paramsUserItem);
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
    if (UserDevice.updateObject(query, params) <= 0) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }

    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("userDeviceId", null);
    UserItem.updateObject(" userDevice.id = :userDeviceId where id = :id", paramsUserItem);
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
    }
    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("id", itemId);
    paramsUserItem.put("isSold", true);
    UserItem.updateObject(" isSold = :isSold where id = :id", paramsUserItem);
    User.updatePointUser(user.id, userItem.item.price.multiply(new BigDecimal("0.5")));
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
    List<Long> itemIds = UserItem.findItemForSell(user.id, request.code, request.number);
    if (itemIds.isEmpty() || itemIds.size() < request.number) {
      throw new BusinessException(ResponseMessageConstants.ITEM_SELL_NOT_ENOUGH);
    }

    Map<String, Object> paramsUserItem = new HashMap<>();
    paramsUserItem.put("ids", itemIds);
    paramsUserItem.put("isSold", true);
//    UserItem.updateObject(" isSold = :isSold where id in (:id)", paramsUserItem);
    if (UserItem.updateObject(" isSold = :isSold where id in (:ids)", paramsUserItem) < request.number) {
      throw new BusinessException(ResponseMessageConstants.ITEM_SELL_NOT_ENOUGH);
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
    UserItem.updateObject(" name = :name where id = :id", paramsDevice);
    return true;
  }
}

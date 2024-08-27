package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.AddItemRequest;
import xyz.telegram.depinalliance.entities.Item;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.entities.UserDevice;
import xyz.telegram.depinalliance.entities.UserItem;

import java.math.BigDecimal;
import java.util.HashMap;
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
  public boolean addItem(User user, AddItemRequest request) throws Exception {
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
    if (!request.isBuy) {
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
      UserItem.create(new UserItem(user, item, userDevice));
      Map<String, Object> params = new HashMap<>();
      params.put("id", userDevice.id);
      params.put("item", request.number);
      UserDevice.updateObject(query + " where id = :id", params);
      userService.mining(user);
      BigDecimal miningPower = item.miningPower.multiply(new BigDecimal(request.number));
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("miningPower", miningPower);
      User.updateUser("miningPower = miningPower + :miningPower where id = :id", paramsUser);
    } else {
      UserItem.create(new UserItem(user, item, null));
    }
    User.updatePointUser(user.id, amount.multiply(new BigDecimal("-1")));
    return true;
  }
}

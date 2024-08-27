package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.DetectDeviceResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author holden on 24-Aug-2024
 */
@ApplicationScoped
public class UserService {
  @Inject
  Logger logger;
  @Inject
  SystemConfigService systemConfigService;

  @Transactional
  public User checkStartUser(Long id, String username, String refCode) {
    User user = User.findById(id);
    if (user == null) {
      user = new User();
      user.id = id;
      user.username = username;
      user.level = new Level(1L);
      user.status = Enums.UserStatus.STARTED;
      User ref = null;
      if (StringUtils.isNotBlank(refCode)) {
        ref = User.findByCode(refCode);
        if (ref != null) {
          User.updatePointUser(ref.id,
            new BigDecimal(Objects.requireNonNull(SystemConfig.findByKey(Enums.Config.POINT_REF))));
        }
      }
      user.ref = ref;
      User.createUser(user);
      UserDevice userDevice = new UserDevice();
      userDevice.user = user;
      userDevice.name = "Device " + 1;
      userDevice.index = 1;
      UserDevice.create(userDevice);
      logger.info("User " + user.username + " created");
      return user;
    }
    if (!user.username.equals(username)) {
      Map<String, Object> params = new HashMap<>();
      params.put("id", id);
      params.put("username", username);
      User.updateUser("username = :username where id = :id", params);
    }
    return user;
  }

  @Transactional
  public Object detectDeviceInfo(User user) throws Exception {
    if (user.status != Enums.UserStatus.STARTED) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    UserDevice userDevice = UserDevice.findByUserAndIndex(user.id, 1);
    String codeCpu = SystemConfig.findByKey(Enums.Config.CPU_DEFAULT);
    Item itemCpu = Item.find("code", codeCpu).firstResult();
    UserItem.create(new UserItem(user, itemCpu, userDevice));

    String codeGpu = SystemConfig.findByKey(Enums.Config.GPU_DEFAULT);
    Item itemGpu = Item.find("code", codeGpu).firstResult();
    UserItem.create(new UserItem(user, itemGpu, userDevice));

    String codeRam = SystemConfig.findByKey(Enums.Config.RAM_DEFAULT);
    Item itemRam = Item.find("code", codeRam).firstResult();
    UserItem.create(new UserItem(user, itemRam, userDevice));

    String codeStorage = SystemConfig.findByKey(Enums.Config.STORAGE_DEFAULT);
    Item itemStorage = Item.find("code", codeStorage).firstResult();
    UserItem.create(new UserItem(user, itemStorage, userDevice));
    Map<String, Object> params = new HashMap<>();
    params.put("id", userDevice.id);

    UserDevice.updateObject(
      "slotCpuUsed = slotCpuUsed + 1, slotRamUsed = slotRamUsed + 1, slotGpuUsed = slotGpuUsed + 1, slotStorageUsed = slotStorageUsed + 1 where id = :id",
      params);
    BigDecimal pointUnClaimed = itemCpu.point.add(itemGpu.point).add(itemRam.point).add(itemStorage.point);
    BigDecimal miningPower = itemCpu.miningPower.add(itemGpu.miningPower).add(itemRam.miningPower)
      .add(itemStorage.miningPower);
    Map<String, Object> paramsUser = new HashMap<>();
    paramsUser.put("id", user.id);
    paramsUser.put("status", Enums.UserStatus.DETECTED_DEVICE_INFO);
    paramsUser.put("pointUnClaimed", pointUnClaimed);
    paramsUser.put("miningPower", miningPower);
    User.updateUser("status = :status, pointUnClaimed = :pointUnClaimed, miningPower = :miningPower where id = :id",
      paramsUser);
    List<DetectDeviceResponse> rs = new ArrayList<>();
    rs.add(new DetectDeviceResponse(Enums.ItemType.CPU, itemCpu.name, itemCpu.point));
    rs.add(new DetectDeviceResponse(Enums.ItemType.GPU, itemGpu.name, itemGpu.point));
    rs.add(new DetectDeviceResponse(Enums.ItemType.RAM, itemRam.name, itemRam.point));
    rs.add(new DetectDeviceResponse(Enums.ItemType.STORAGE, itemStorage.name, itemStorage.point));
    return rs;
  }

  @Transactional
  public BigDecimal claimRewardNewUser(User user) throws Exception {
    if (user.status != Enums.UserStatus.DETECTED_DEVICE_INFO) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    Map<String, Object> paramsUser = new HashMap<>();
    paramsUser.put("id", user.id);
    paramsUser.put("status", Enums.UserStatus.CLAIMED);
    paramsUser.put("point", user.pointUnClaimed);
    paramsUser.put("maximumPower", new BigDecimal(Utils.getRandomNumber(40000, 50000)));
    User.updateUser("status = :status, point = :point, pointUnClaimed = 0, maximumPower = :maximumPower where id = :id",
      paramsUser);
    return Utils.stripDecimalZeros(user.pointUnClaimed);
  }

  public BigDecimal mining(User user) throws Exception {
    synchronized (user.id.toString().intern()) {
      BigDecimal res = mining(user, Utils.getCalendar().getTimeInMillis() / 1000);
      Thread.sleep(200);
      return res;
    }
  }

  @Transactional
  public BigDecimal claim(User user) throws Exception {
    synchronized (user.id.toString().intern()) {
      if (user.status != Enums.UserStatus.MINING) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
      mining(user);
      user = User.findById(user.id);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("point", user.pointUnClaimed);
      User.updateUser("point = point + :point, pointUnClaimed = pointUnClaimed - :point where id = :id", paramsUser);
      return Utils.stripDecimalZeros(user.pointUnClaimed);
    }
  }

  @Transactional
  public BigDecimal changeMiningPower(User user, BigDecimal miningPower) throws Exception {
    synchronized (user.id.toString().intern()) {
      if (user.status != Enums.UserStatus.MINING) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
      mining(user);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("miningPower", miningPower);
      User.updateUser("miningPower = miningPower + :miningPower where id = :id", paramsUser);
      return Utils.stripDecimalZeros(user.pointUnClaimed);
    }
  }

  @Transactional
  public BigDecimal mining(User user, long time) throws Exception {
    if (user.status != Enums.UserStatus.CLAIMED && user.status != Enums.UserStatus.MINING) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    if (user.status == Enums.UserStatus.CLAIMED) {
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("status", Enums.UserStatus.MINING);
      paramsUser.put("timeStartMining", time);
      User.updateUser("status = :status, timeStartMining = :timeStartMining where id = :id", paramsUser);
      return BigDecimal.ZERO;
    }
    BigDecimal pointUnClaimed = user.miningPower.divide(new BigDecimal(3600), 18, RoundingMode.FLOOR)
      .multiply(new BigDecimal(time - user.timeStartMining));
    if (user.pointUnClaimed.add(pointUnClaimed).compareTo(user.maximumPower) > 0) {
      pointUnClaimed = user.maximumPower.subtract(user.pointUnClaimed);
    }
    Map<String, Object> paramsUser = new HashMap<>();
    paramsUser.put("id", user.id);
    paramsUser.put("pointUnClaimed", pointUnClaimed);
    paramsUser.put("timeStartMining", time);
    User.updateUser(
      "pointUnClaimed = pointUnClaimed + :pointUnClaimed, timeStartMining = :timeStartMining where id = :id",
      paramsUser);
    return Utils.stripDecimalZeros(pointUnClaimed);
  }

  public boolean upgradeLevel(User user) throws Exception {
    synchronized (user.id.toString().intern()) {
      if (user.status != Enums.UserStatus.CLAIMED && user.status != Enums.UserStatus.MINING) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
      long maxLevel = Level.maxLevel();
      if(user.level.id >= maxLevel)
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      Level nextLevel = Level.findById(user.level.id+1);
      //TODO: Subtract point, exp of user by nextLevel

      //Update level
      User.updateLevel(user.id, maxLevel);
      UserLevelHistory history = new UserLevelHistory();
      history.create();
      history.userId = user.id;
      history.levelCurrent = user.level.id;
      history.levelUpgrade = nextLevel.id;
      history.pointUsed = nextLevel.point;
      history.expUsed = nextLevel.exp;
      UserLevelHistory.createHistory(history);
      return true;
    }
  }
  public boolean upgradeSkill(User user, Long skillId) throws Exception {
    synchronized (user.id.toString().intern()) {
      if (user.status != Enums.UserStatus.CLAIMED && user.status != Enums.UserStatus.MINING) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
      UserSkill userSkill = UserSkill.findByUserIdAndSkillId(user.id, skillId)
              .orElseThrow(() -> new BusinessException(ResponseMessageConstants.HAS_ERROR));
      Integer maxLevel = SkillLevel.getMaxLevel(skillId);
      if(userSkill.skill.id >= maxLevel)
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      SkillLevel skillLevelNext = SkillLevel.findBySkillAndLevel(userSkill.skill.id, userSkill.level+1)
              .orElseThrow(() -> new BusinessException(ResponseMessageConstants.HAS_ERROR));
      //TODO: Subtract point to upgrade skill

      //Update level
      UserSkill.updateLevel(user.id, skillId, maxLevel);
//      UserLevelHistory history = new UserLevelHistory();
//      history.create();
//      history.userId = user.id;
//      history.levelCurrent = user.level.id;
//      history.levelUpgrade = nextLevel.id;
//      history.pointUsed = nextLevel.point;
//      history.expUsed = nextLevel.exp;
//      UserLevelHistory.createHistory(history);
      return true;
    }
  }
}

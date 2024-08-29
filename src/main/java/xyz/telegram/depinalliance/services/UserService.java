package xyz.telegram.depinalliance.services;

import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.DetectDeviceResponse;
import xyz.telegram.depinalliance.common.models.response.UserSkillResponse;
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

  @Transactional
  public User checkStartUser(Long id, String username, String refCode) {
    User user = User.findById(id);
    if (user == null) {
      user = new User();
      user.id = id;
      user.username = username;
      user.level = new Level(1L);
      user.status = Enums.UserStatus.STARTED;
      user.avatar = SystemConfig.findByKey(Enums.Config.AVATAR_DEFAULT);
      User ref = null;
      if (StringUtils.isNotBlank(refCode)) {
        ref = User.findByCode(refCode);
        if (ref != null) {
          User.updatePointUser(ref.id,
            new BigDecimal(Objects.requireNonNull(SystemConfig.findByKey(Enums.Config.POINT_REF))));
          user.pointRef = new BigDecimal(Objects.requireNonNull(SystemConfig.findByKey(Enums.Config.POINT_REF)));
        }
      }
      user.ref = ref;
      User.createUser(user);
      UserDevice userDevice = new UserDevice();
      userDevice.user = user;
      userDevice.name = "Device " + 1;
      userDevice.index = 1;
      UserDevice.create(userDevice);
      UserSkill.initUserSkill(user, Skill.findAll().list());
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
    paramsUser.put("maximumPower",
      new BigDecimal(Objects.requireNonNull(SystemConfig.findByKey(Enums.Config.MAX_MINING_POWER_DEFAULT))));
    User.updateUser("status = :status, point = :point, pointUnClaimed = 0, maximumPower = :maximumPower where id = :id",
      paramsUser);
    return Utils.stripDecimalZeros(user.pointUnClaimed);
  }

  @Transactional
  public BigDecimal startContributing(User user) throws Exception {
    if (user.status != Enums.UserStatus.CLAIMED) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    Map<String, Object> paramsUser = new HashMap<>();
    paramsUser.put("id", user.id);
    paramsUser.put("status", Enums.UserStatus.MINING);
    paramsUser.put("timeStartMining", Utils.getCalendar().getTimeInMillis() / 1000);
    User.updateUser("status = :status, timeStartMining = :timeStartMining where id = :id", paramsUser);
    return BigDecimal.ZERO;
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

      BigDecimal pointUnClaimed = user.pointUnClaimed.multiply(bonusClaim());
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("point", pointUnClaimed);
      paramsUser.put("pointUnClaimed", user.pointUnClaimed);
      User.updateUser("point = point + :point, pointUnClaimed = pointUnClaimed - :pointUnClaimed where id = :id",
        paramsUser);
      Long userRefId;
      if (user.ref != null) {
        userRefId = user.ref.id;
      } else {
        userRefId = Long.valueOf(Objects.requireNonNull(SystemConfig.findByKey(Enums.Config.ROOT_POINT_CLAIM)));
      }
      BigDecimal refPointClaim = new BigDecimal(
        Objects.requireNonNull(SystemConfig.findByKey(Enums.Config.REF_POINT_CLAIM)));
      Map<String, Object> paramsUserRef = new HashMap<>();
      paramsUserRef.put("id", userRefId);
      paramsUserRef.put("point", pointUnClaimed.multiply(refPointClaim));
      User.updateUser("point = point + :point where id = :id", paramsUserRef);
      return Utils.stripDecimalZeros(pointUnClaimed);
    }
  }

  public BigDecimal bonusClaim() {
    int a = new Random().nextInt(100);
    if (a < 5) {
      // 5% chance
      a = new Random().nextInt(100);
      if (a < 65) {
        // 65% chance
        return new BigDecimal("1.05");
      } else if (a < 85) {
        // 20% chance
        return new BigDecimal("1.1");
      } else if (a < 95) {
        // 10% chance
        return new BigDecimal("1.15");
      } else if (a < 99) {
        // 4%
        return new BigDecimal("1.35");
      } else {
        //1 %
        return new BigDecimal("1.4");
      }
    }
    return BigDecimal.ONE;
  }

  @Transactional
  public BigDecimal changeMiningPower(User user, BigDecimal miningPower) throws Exception {
    synchronized (user.id.toString().intern()) {
      mining(user);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("miningPower", miningPower);
      User.updateUser("miningPower = miningPower + :miningPower where id = :id", paramsUser);
      if (user.league != null) {
        Map<String, Object> paramsLeague = new HashMap<>();
        paramsLeague.put("id", user.league.id);
        paramsLeague.put("miningPower", miningPower);
        League.updateObject("totalMining = totalMining + :miningPower where id = :id", paramsLeague);
      }
      return Utils.stripDecimalZeros(user.miningPower.add(user.miningPower));
    }
  }

  @Transactional
  public BigDecimal mining(User user, long time) throws Exception {
    if (user.status != Enums.UserStatus.MINING) {
      return BigDecimal.ZERO;
    }
    BigDecimal pointUnClaimed = user.miningPower.divide(new BigDecimal(3600), 18, RoundingMode.FLOOR)
      .multiply(new BigDecimal(time - user.timeStartMining)).multiply(user.rateMining);
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

  public List<UserSkillResponse> getUserSkill(Long userId) {
    return UserSkill.findByUserId(userId);
  }

  //  public boolean upgradeLevel(User user) throws Exception {
  //    synchronized (user.id.toString().intern()) {
  //      if (user.status != Enums.UserStatus.CLAIMED && user.status != Enums.UserStatus.MINING) {
  //        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  //      }
  //      long maxLevel = Level.maxLevel();
  //      if (user.level.id >= maxLevel)
  //        throw new BusinessException(ResponseMessageConstants.USER_MAX_LEVEL);
  //      Level nextLevel = Level.findById(user.level.id + 1);
  //      if (user.point.compareTo(nextLevel.point) < 0 || user.xp.compareTo(nextLevel.exp) < 0)
  //        throw new BusinessException(ResponseMessageConstants.USER_BALANCE_NOT_ENOUGH);
  //      if (!User.updateLevel(user.id, nextLevel.id, maxLevel, nextLevel.point.multiply(new BigDecimal(-1)),
  //        nextLevel.exp.multiply(new BigDecimal(-1))))
  //        throw new BusinessException(ResponseMessageConstants.USER_UPGRADE_LEVEL_FAILED);
  //      HistoryUpgradeLevel history = new HistoryUpgradeLevel();
  //      history.create();
  //      history.userId = user.id;
  //      history.levelCurrent = user.level.id;
  //      history.levelUpgrade = nextLevel.id;
  //      history.pointUsed = nextLevel.point;
  //      history.expUsed = nextLevel.exp;
  //      HistoryUpgradeLevel.createHistory(history);
  //      return true;
  //    }
  //  }

  public boolean upgradeSkill(User user, Long skillId) throws Exception {
    synchronized (user.id.toString().intern()) {
      if (user.status != Enums.UserStatus.CLAIMED && user.status != Enums.UserStatus.MINING) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
      Skill skill = (Skill) Skill.findByIdOptional(skillId)
              .orElseThrow(() -> new BusinessException(ResponseMessageConstants.SKILL_NOT_FOUND));
      UserSkill userSkill = UserSkill.findByUserIdAndSkillId(user.id, skillId)
        .orElseThrow(() -> new BusinessException(ResponseMessageConstants.USER_SKILL_NOT_FOUND));
      if (userSkill.skill.id >= skill.maxLevel)
        throw new BusinessException(ResponseMessageConstants.USER_SKILL_MAX_LEVEL);
      if(userSkill.timeUpgrade > Utils.getCalendar().getTimeInMillis())
        throw new BusinessException(ResponseMessageConstants.USER_SKILL_WAITING_UPGRADE);
      SkillLevel userSkillNext = SkillLevel.findBySkillAndLevel(userSkill.skill.id, userSkill.level + 1)
        .orElseThrow(() -> new BusinessException(ResponseMessageConstants.USER_SKILL_MAX_LEVEL));
      if(user.pointSkill.compareTo(userSkillNext.feeUpgrade) < 0)
        throw new BusinessException(ResponseMessageConstants.USER_POINT_NOT_ENOUGH);
      if(!User.updatePointSkill(user.id, userSkillNext.feeUpgrade.multiply(new BigDecimal(-1))))
        throw new BusinessException(ResponseMessageConstants.USER_POINT_NOT_ENOUGH);
      long currentTime = Utils.getCalendar().getTimeInMillis();
      long timeUpgrade = currentTime + 1000 * userSkillNext.timeWaitUpgrade;
      if (!UserSkill.upgradeSkillPending(user.id, skillId, timeUpgrade, currentTime))
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      HistoryUpgradeSkill history = new HistoryUpgradeSkill();
      history.create();
      history.userId = user.id;
      history.skillId = userSkill.skill.id;
      history.levelCurrent = userSkill.level;
      history.levelUpgrade = userSkillNext.level;
      history.rateMining = userSkillNext.rateMining;
      history.ratePurchase = userSkillNext.ratePurchase;
      history.rateReward = userSkillNext.rateReward;
      history.feeUpgrade = userSkillNext.feeUpgrade;
      history.timeWaitUpgrade = userSkillNext.timeWaitUpgrade;
      history.timeUpgrade = timeUpgrade;
      HistoryUpgradeSkill.createHistory(history);
      return true;
    }
  }
  @Transactional
  public void updateSkillLevelForUser(HistoryUpgradeSkill his) {
    boolean status = UserSkill.updateLevel(his.userId, his.skillId, his.levelUpgrade);
    if(status) {
      User.updateRate(his.userId, his.rateMining, his.ratePurchase, his.rateReward);
    }
    HistoryUpgradeSkill.update("status=1 where id = :id", Parameters.with("id", his.id));
  }
  public void updateLevelByExp(long userId) {
    User user = User.findById(userId);
    Long maxLevel = Level.maxLevel();
    Level level = Level.getLevelBeExp(user.xp);
    if(null!=level && level.id - user.level.id > 0 && level.id < maxLevel) {
      User.updateLevelAndPointSkill(userId, level.id, new BigDecimal(level.id - user.level.id));
    }
  }
}

package xyz.telegram.depinalliance.services;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.DeviceInfo;
import xyz.telegram.depinalliance.common.models.request.SettingRequest;
import xyz.telegram.depinalliance.common.models.response.ClaimResponse;
import xyz.telegram.depinalliance.common.models.response.SettingResponse;
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
  @Inject
  LeagueService leagueService;
  @Inject
  RedisService redisService;

  @Transactional
  public User checkStartUser(Long id, String username, String refCode, String league, Boolean isPremium) {
    User user = User.findById(id);
    isPremium = isPremium != null && isPremium;
    if (user == null) {
      user = new User();
      user.id = id;
      user.username = username;
      user.level = new Level(1L);
      user.status = Enums.UserStatus.STARTED;
      user.avatar = redisService.findConfigByKey(Enums.Config.AVATAR_DEFAULT);
      user.isPremium = isPremium;
      User ref = null;
      if (StringUtils.isNotBlank(refCode)) {
        ref = User.findByCode(refCode);
        if (ref != null) {
          BigDecimal pointRef = new BigDecimal(
            Objects.requireNonNull(redisService.findConfigByKey(Enums.Config.POINT_REF)));
          user.pointRef = pointRef;
          Map<String, Object> params = new HashMap<>();
          params.put("id", ref.id);
          params.put("point", pointRef);
          User.updateUser(
            "point = point + :point, pointEarned = pointEarned + :point , totalFriend = totalFriend + 1 where id = :id",
            params);
        }
      }
      user.ref = ref;
      User.createUser(user);
      if (StringUtils.isNotBlank(league) && StringUtils.isNotBlank(refCode) && ref != null && ref.league != null) {
        refCode += " request league " + league;
        leagueService.joinLeague(user, ref.league.code);
      }

      UserDevice userDevice = new UserDevice();
      userDevice.user = user;
      userDevice.name = "Device " + 1;
      userDevice.index = 1;
      UserDevice.create(userDevice);
      UserSkill.initUserSkill(user, Skill.findAll().list());
      logger.info("User " + user.username + " created with ref code " + refCode);
      return user;
    }
    if (StringUtils.isNotBlank(league) && StringUtils.isNotBlank(refCode) && user.league == null) {
      User ref = User.findByCode(refCode);
      if (ref != null && ref.league != null) {
        logger.info("user " + user.username + " request league " + ref.league.code + " with ref code " + ref.id);
        leagueService.joinLeague(user, ref.league.code);
      }
    }
    boolean hasChange = false;
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    List<String> sql = new ArrayList<>();
    if (!user.username.equals(username)) {
      hasChange = true;
      params.put("username", username);
      sql.add("username = :username");
    }
    boolean userPremium = user.isPremium != null && user.isPremium;
    if (userPremium != isPremium) {
      hasChange = true;
      params.put("isPremium", isPremium);
      sql.add("isPremium = :isPremium");
    }

    if (hasChange) {
      User.updateUser(String.join(" , ", sql) + " where id = :id", params);
    }
    return user;
  }

  @Transactional
  public Object detectDeviceInfo(User user, String deviceInfo) {
    if (user.status != Enums.UserStatus.STARTED) {
      return BigDecimal.ZERO;
    }
    UserDevice userDevice = UserDevice.findByUserAndIndex(user.id, 1);
    String codeCpu = redisService.findConfigByKey(Enums.Config.CPU_DEFAULT);
    Item itemCpu = Item.find("code", codeCpu).firstResult();
    UserItem.create(new UserItem(user, itemCpu, userDevice));
    //    String codeGpu = systemConfigService.findConfigByKey(Enums.Config.GPU_DEFAULT);
    //    Item itemGpu = Item.find("code", codeGpu).firstResult();
    //    UserItem.create(new UserItem(user, itemGpu, userDevice));

    String codeRam = redisService.findConfigByKey(Enums.Config.RAM_DEFAULT);
    Item itemRam = Item.find("code", codeRam).firstResult();
    UserItem.create(new UserItem(user, itemRam, userDevice));

    String codeStorage = redisService.findConfigByKey(Enums.Config.STORAGE_DEFAULT);
    Item itemStorage = Item.find("code", codeStorage).firstResult();
    UserItem.create(new UserItem(user, itemStorage, userDevice));

    BigDecimal miningPower = itemCpu.miningPower.add(itemRam.miningPower).add(itemStorage.miningPower);
    Map<String, Object> params = new HashMap<>();
    params.put("id", userDevice.id);
    params.put("totalMiningPower", miningPower);
    UserDevice.updateObject(
      "slotCpuUsed = slotCpuUsed + 1, slotRamUsed = slotRamUsed + 1, slotStorageUsed = slotStorageUsed + 1, totalMiningPower = :totalMiningPower where id = :id",
      params);

    BigDecimal pointUnClaimed = new BigDecimal(5000);
    if (StringUtils.isBlank(deviceInfo)) {
      deviceInfo = "Unknown Device";
    }

    Map<String, Object> paramsUser = new HashMap<>();
    paramsUser.put("id", user.id);
    paramsUser.put("status", Enums.UserStatus.DETECTED_DEVICE_INFO);
    paramsUser.put("pointUnClaimed", pointUnClaimed);
    paramsUser.put("miningPower", miningPower);
    paramsUser.put("detectDevice", deviceInfo);
    User.updateUser(
      "status = :status, pointUnClaimed = :pointUnClaimed, pointBonus = :pointUnClaimed, miningPower = :miningPower, miningPowerReal = :miningPower, totalDevice = 1, detectDevice = :detectDevice where id = :id",
      paramsUser);
    return pointUnClaimed;
  }

  @Transactional
  public Object detectDeviceInfo1(User user, Object request) throws Exception {
    if (user.status != Enums.UserStatus.STARTED) {
      return BigDecimal.ZERO;
    }
    if (request == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    BigDecimal pointUnClaimed = BigDecimal.ZERO;
    String deviceInfoFinal = "";
    Map<String, Object> paramsUser = new HashMap<>();
    try {
      String deviceInfoStr = Utils.convertObjectToString(request);
      DeviceInfo deviceInfo = Utils.toObject(deviceInfoStr, DeviceInfo.class);

      DevicePoint devicePoint = null;
      if (deviceInfo == null || StringUtils.isBlank(deviceInfo.detectedModel) || StringUtils.isBlank(
        deviceInfo.platform)) {
        //        pointUnClaimed = DevicePoint.find("select min(point) from DevicePoint").project(BigDecimal.class).firstResult();
      } else if (deviceInfo.platform.equalsIgnoreCase("IOS")) {
        String model = deviceInfo.detectedModel.trim().toLowerCase();
        String[] models = model.split(",");
        Set<String> modelsFinal = new HashSet<>();
        for (String modelName : models) {
          modelsFinal.add(modelName.trim().toLowerCase());
        }
        devicePoint = DevicePoint.find("platform = 'IOS' and lower(name) in (?1)", Sort.descending("point"),
          modelsFinal).firstResult();
      } else if (deviceInfo.platform.equalsIgnoreCase(
        "Android") && deviceInfo.clientHints != null && StringUtils.isNotBlank(deviceInfo.clientHints.model)) {
        String model = deviceInfo.clientHints.model.toLowerCase().trim();
        devicePoint = DevicePoint.find("platform = 'Android' and lower(name) like ?1", "%" + model + "%").firstResult();
        if (devicePoint == null && model.contains("sm-")) {
          model = model.replace("sm-", "");
          devicePoint = DevicePoint.find("platform = 'Android' and lower(name) like ?1", "%" + model + "%")
            .firstResult();
          if (devicePoint == null) {
            model = model.substring(0, model.length() - 1);
            devicePoint = DevicePoint.find("platform = 'Android' and lower(name) like ?1", "%" + model + "%")
              .firstResult();
            if (devicePoint == null) {
              model = model.substring(0, model.length() - 1);
              devicePoint = DevicePoint.find("platform = 'Android' and lower(name) like ?1", "%" + model + "%")
                .firstResult();
            }
          }
        }
      }
      if (devicePoint == null) {
        //        pointUnClaimed = DevicePoint.find("select min(point) from DevicePoint").project(BigDecimal.class).firstResult();
      } else {
        deviceInfoFinal = devicePoint.name;
        pointUnClaimed = devicePoint.point;
      }
      //      System.out.println(pointUnClaimed);
      paramsUser.put("devicePlatform", deviceInfo.platform);
      paramsUser.put("deviceModel", deviceInfoStr);
    } catch (Exception e) {
      throw new Exception(ResponseMessageConstants.DATA_INVALID);
    }

    if (StringUtils.isBlank(deviceInfoFinal)) {
      deviceInfoFinal = "Unknown Device";
    }
    if (pointUnClaimed.compareTo(new BigDecimal(6000)) <= 0) {
      pointUnClaimed = new BigDecimal(6000).add(
        new BigDecimal(Utils.getRandomNumber(0, 200)).multiply(new BigDecimal(10)));
    }
    UserDevice userDevice = UserDevice.findByUserAndIndex(user.id, 1);
    String codeCpu = redisService.findConfigByKey(Enums.Config.CPU_DEFAULT);
    Item itemCpu = redisService.findItemByCode(codeCpu);
    UserItem.create(new UserItem(user, itemCpu, userDevice));

    String codeRam = redisService.findConfigByKey(Enums.Config.RAM_DEFAULT);
    Item itemRam = redisService.findItemByCode(codeRam);
    UserItem.create(new UserItem(user, itemRam, userDevice));

    String codeStorage = redisService.findConfigByKey(Enums.Config.STORAGE_DEFAULT);
    Item itemStorage = redisService.findItemByCode(codeStorage);
    UserItem.create(new UserItem(user, itemStorage, userDevice));

    BigDecimal miningPower = itemCpu.miningPower.add(itemRam.miningPower).add(itemStorage.miningPower);
    Map<String, Object> params = new HashMap<>();
    params.put("id", userDevice.id);
    params.put("totalMiningPower", miningPower);
    UserDevice.updateObject(
      "slotCpuUsed = slotCpuUsed + 1, slotRamUsed = slotRamUsed + 1, slotStorageUsed = slotStorageUsed + 1, totalMiningPower = :totalMiningPower where id = :id",
      params);
    paramsUser.put("id", user.id);
    paramsUser.put("status", Enums.UserStatus.DETECTED_DEVICE_INFO);
    paramsUser.put("pointUnClaimed", pointUnClaimed);
    paramsUser.put("miningPower", miningPower);
    paramsUser.put("detectDevice", deviceInfoFinal);

    User.updateUser(
      "status = :status, pointUnClaimed = :pointUnClaimed, pointBonus = :pointUnClaimed, miningPower = :miningPower, miningPowerReal = :miningPower, totalDevice = 1, detectDevice = :detectDevice, devicePlatform = :devicePlatform, deviceModel = :deviceModel where id = :id",
      paramsUser);
    return Utils.stripDecimalZeros(pointUnClaimed);
  }

  @Transactional
  public BigDecimal claimRewardNewUser(User user) {
    if (user.status != Enums.UserStatus.DETECTED_DEVICE_INFO) {
      return BigDecimal.ZERO;
    }
    user.level = redisService.findLevelById(user.level.id);
    Map<String, Object> paramsUser = new HashMap<>();
    paramsUser.put("id", user.id);
    paramsUser.put("status", Enums.UserStatus.CLAIMED);
    paramsUser.put("point", user.pointUnClaimed);
    paramsUser.put("maximumPower", user.level.maxMiningPower);
    paramsUser.put("firstLoginTime", Utils.getCalendar().getTimeInMillis());
    User.updateUser(
      "status = :status, point = :point, pointEarned = :point, pointUnClaimed = 0, maximumPower = :maximumPower, firstLoginTime = :firstLoginTime where id = :id",
      paramsUser);
    return Utils.stripDecimalZeros(user.pointUnClaimed);
  }

  @Transactional
  public BigDecimal startContributing(User user) {
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
      return res;
    }
  }

  @Transactional
  public ClaimResponse claim(User user) throws Exception {
    synchronized (user.id.toString().intern()) {
      if (user.status != Enums.UserStatus.MINING) {
        return new ClaimResponse(BigDecimal.ZERO, BigDecimal.ZERO);
      }
      mining(user);
      user = User.findById(user.id);
      Long userRefId;
      BigDecimal refPointClaim = BigDecimal.ZERO;
      if (user.ref != null) {
        userRefId = user.ref.id;
        refPointClaim = redisService.findRefPercentClaimById(userRefId, true);
      } else {
        userRefId = Long.valueOf(Objects.requireNonNull(redisService.findConfigByKey(Enums.Config.ROOT_POINT_CLAIM)));
      }
      refPointClaim = refPointClaim.compareTo(BigDecimal.ZERO) <= 0 ?
        new BigDecimal(Objects.requireNonNull(redisService.findConfigByKey(Enums.Config.REF_POINT_CLAIM))) :
        refPointClaim;
      BigDecimal rateBonus = user.rateReward.subtract(BigDecimal.ONE).multiply(new BigDecimal(100));
      BigDecimal percentBonus = bonusClaim(rateBonus.intValue());
      BigDecimal pointBonus = user.pointUnClaimed.multiply(percentBonus);
      BigDecimal pointUnClaimed = user.pointUnClaimed.multiply(percentBonus.add(BigDecimal.ONE));
      BigDecimal pointRef = pointUnClaimed.multiply(refPointClaim);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("point", pointUnClaimed);
      paramsUser.put("pointRef", pointRef);
      if (pointBonus.compareTo(BigDecimal.ZERO) > 0) {
        ClaimRewardHistory claimRewardHistory = new ClaimRewardHistory();
        claimRewardHistory.user = user;
        claimRewardHistory.claimNumber = user.claimNumber + 1;
        claimRewardHistory.pointClaimed = pointUnClaimed;
        claimRewardHistory.pointClaim = user.pointUnClaimed;
        claimRewardHistory.rateReward = rateBonus.add(
          new BigDecimal(redisService.findConfigByKey(Enums.Config.BONUS_REWARD_DEFAULT)));
        claimRewardHistory.percentBonus = percentBonus;
        claimRewardHistory.pointBonus = pointBonus;
        claimRewardHistory.create();
        claimRewardHistory.persist();
      }
      User.updateUser(
        "point = point + :point, pointClaimed = pointClaimed + pointUnClaimed, pointEarned = pointEarned + :point, pointUnClaimed = 0, pointRef = pointRef + :pointRef, claimNumber = claimNumber + 1  where id = :id",
        paramsUser);
      Map<String, Object> paramsUserRef = new HashMap<>();
      paramsUserRef.put("id", userRefId);
      paramsUserRef.put("point", pointRef);
      User.updateUser("point = point + :point, pointEarned = pointEarned + :point  where id = :id", paramsUserRef);

      return new ClaimResponse(pointUnClaimed, pointBonus);
    }
  }

  public BigDecimal bonusClaim(int rate) {
    int a = new Random().nextInt(100);
    if (a < (redisService.getSystemConfigInt(Enums.Config.BONUS_REWARD_DEFAULT) + rate)) {
      // 5% chance
      a = new Random().nextInt(100);
      if (a < 65) {
        // 65% chance
        return new BigDecimal("0.05");
      } else if (a < 85) {
        // 20% chance
        return new BigDecimal("0.1");
      } else if (a < 95) {
        // 10% chance
        return new BigDecimal("0.15");
      } else if (a < 99) {
        // 4%
        return new BigDecimal("0.35");
      } else {
        //1 %
        return new BigDecimal("0.4");
      }
    }
    return BigDecimal.ZERO;
  }

  @Transactional
  public void changeMiningPower(User user, BigDecimal miningPower) throws Exception {
    synchronized (user.id.toString().intern()) {
      mining(user);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("miningPower", miningPower);
      User.updateUser(
        "miningPower = miningPower + :miningPower, miningPowerReal = (select COALESCE(sum(ui.item.miningPower),0) from UserItem ui where ui.user.id = :id and userDevice is not null ) * rateMining where id = :id",
        paramsUser);
      //      if (user.league != null) {
      //        Map<String, Object> paramsLeague = new HashMap<>();
      //        paramsLeague.put("id", user.league.id);
      //        paramsLeague.put("miningPower", miningPower.multiply(user.rateMining));
      //        League.updateObject("totalMining = totalMining + :miningPower where id = :id", paramsLeague);
      //      }
    }
  }

  @Transactional
  public void changeMiningPower(User user, BigDecimal miningPowerOld, BigDecimal miningPowerNew) throws Exception {
    synchronized (user.id.toString().intern()) {
      mining(user);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("miningPowerOld", miningPowerOld);
      paramsUser.put("miningPowerNew", miningPowerNew);
      User.updateUser(
        "miningPower = miningPower - :miningPowerOld + :miningPowerNew, miningPowerReal = (select COALESCE(sum(ui.item.miningPower),0) from UserItem ui where ui.user.id = :id and userDevice is not null ) * rateMining where id = :id",
        paramsUser);
      //      if (user.league != null) {
      //        Map<String, Object> paramsLeague = new HashMap<>();
      //        paramsLeague.put("id", user.league.id);
      //        paramsLeague.put("miningPowerOld", miningPowerOld.multiply(user.rateMining));
      //        paramsLeague.put("miningPowerNew", miningPowerNew.multiply(user.rateMining));
      //        League.updateObject("totalMining = totalMining - :miningPowerOld + :miningPowerNew where id = :id",
      //          paramsLeague);
      //      }
    }
  }

  @Transactional
  public BigDecimal mining(User user, long time) throws Exception {
    if (user.status != Enums.UserStatus.MINING) {
      return BigDecimal.ZERO;
    }
    BigDecimal pointUnClaimed = user.miningPowerReal.divide(new BigDecimal(3600), 18, RoundingMode.FLOOR)
      .multiply(new BigDecimal(time - user.timeStartMining));
    BigDecimal maximumPower = user.maximumPower.multiply(user.rateCapacity);
    if (user.pointUnClaimed.add(pointUnClaimed).compareTo(maximumPower) > 0) {
      pointUnClaimed = maximumPower.subtract(user.pointUnClaimed);
    }
    Map<String, Object> paramsUser = new HashMap<>();
    paramsUser.put("id", user.id);
    paramsUser.put("pointUnClaimed", pointUnClaimed);
    paramsUser.put("timeStartMining", time);
    User.updateUser(
      "pointUnClaimed = pointUnClaimed + :pointUnClaimed, timeStartMining = :timeStartMining where id = :id and pointUnClaimed + :pointUnClaimed >= 0",
      paramsUser);
    return Utils.stripDecimalZeros(pointUnClaimed);
  }

  public List<UserSkillResponse> getUserSkill(Long userId) {
    return UserSkill.findByUserId(userId);
  }

  public boolean upgradeSkill(User user, Long skillId) throws Exception {
    synchronized (user.id.toString().intern()) {
      if (user.status != Enums.UserStatus.CLAIMED && user.status != Enums.UserStatus.MINING) {
        throw new BusinessException(ResponseMessageConstants.USER_NOT_ACTIVE);
      }
      Skill skill = (Skill) Skill.findByIdOptional(skillId)
        .orElseThrow(() -> new BusinessException(ResponseMessageConstants.SKILL_NOT_FOUND));
      UserSkill userSkill = UserSkill.findByUserIdAndSkillId(user.id, skillId)
        .orElseThrow(() -> new BusinessException(ResponseMessageConstants.USER_SKILL_NOT_FOUND));
      if (userSkill.skill.id >= skill.maxLevel)
        throw new BusinessException(ResponseMessageConstants.USER_SKILL_MAX_LEVEL);
      if (userSkill.timeUpgrade > Utils.getCalendar().getTimeInMillis())
        throw new BusinessException(ResponseMessageConstants.USER_SKILL_WAITING_UPGRADE);
      SkillLevel userSkillNext = SkillLevel.findBySkillAndLevel(userSkill.skill.id, userSkill.level + 1)
        .orElseThrow(() -> new BusinessException(ResponseMessageConstants.USER_SKILL_MAX_LEVEL));
      if (user.pointSkill.compareTo(userSkillNext.feeUpgrade) < 0)
        throw new BusinessException(ResponseMessageConstants.USER_POINT_SKILL_NOT_ENOUGH);
      SkillPoint skillPoint = SkillPoint.getPointRequire(user.id);
      if (skillPoint != null && user.point.compareTo(skillPoint.point) < 0)
        throw new BusinessException(ResponseMessageConstants.USER_POINT_NOT_ENOUGH);
      if (!User.updatePointSkillAndPoint(user.id, userSkillNext.feeUpgrade.multiply(new BigDecimal(-1)),
        skillPoint.point.multiply(new BigDecimal(-1))))
        throw new BusinessException(ResponseMessageConstants.USER_POINT_OR_POINT_SKILL_NOT_ENOUGH);
      long currentTime = Utils.getCalendar().getTimeInMillis();
      long currentDiscount = user.rateCountDown.multiply(new BigDecimal(100)).longValue();
      long timeUpgrade = currentTime + ((1000 * skillPoint.upgradeTime * currentDiscount) / 100);
      if (!UserSkill.upgradeSkillPending(user.id, skillId, timeUpgrade, currentTime))
        throw new BusinessException(ResponseMessageConstants.USER_SKILL_ANOTHER_WAITING_UPGRADE);
      HistoryUpgradeSkill history = new HistoryUpgradeSkill();
      history.create();
      history.userId = user.id;
      history.skillId = userSkill.skill.id;
      history.levelCurrent = userSkill.level;
      history.levelUpgrade = userSkillNext.level;
      history.rateMining = userSkillNext.rateMining;
      history.ratePurchase = userSkillNext.ratePurchase;
      history.rateReward = userSkillNext.rateReward;
      history.rateCountDown = userSkillNext.rateCountDown;
      history.rateCapacity = userSkillNext.rateCapacity;
      history.feeUpgrade = userSkillNext.feeUpgrade;
      history.feePointUpgrade = skillPoint.point;
      history.timeWaitUpgrade = skillPoint.upgradeTime;
      history.timeUpgrade = timeUpgrade;
      HistoryUpgradeSkill.createHistory(history);
      return true;
    }
  }

  @Transactional
  public void updateSkillLevelForUser(HistoryUpgradeSkill his) {
    boolean status = UserSkill.updateLevel(his.userId, his.skillId, his.levelUpgrade);
    if (status) {
      if (his.rateMining != null && his.rateMining.compareTo(BigDecimal.ZERO) > 0) {
        //        User user = User.findById(his.userId);
        //        if (user.league != null) {
        //          Map<String, Object> paramsLeague = new HashMap<>();
        //          paramsLeague.put("id", user.league.id);
        //          paramsLeague.put("miningPowerOld", user.miningPower.multiply(user.rateMining));
        //          paramsLeague.put("miningPowerNew", user.miningPower.multiply(user.rateMining.add(his.rateMining)));
        //          League.updateObject("totalMining = totalMining - :miningPowerOld + :miningPowerNew where id = :id",
        //            paramsLeague);
        //        }
      }
      User.updateRate(his.userId, his.rateMining, his.ratePurchase, his.rateReward, his.rateCountDown,
        his.rateCapacity);
      if (his.rateMining != null && his.rateMining.compareTo(BigDecimal.ZERO) > 0) {
        User.updateMiningPowerReal(his.userId);
      }
    }
    HistoryUpgradeSkill.update("status=1 where id = :id", Parameters.with("id", his.id));
  }

  @Transactional
  public boolean setting(long userId, SettingRequest request) {
    SettingResponse settingResponse = redisService.findSettingUserById(userId);
    Enums.UserSettings settings = Enums.UserSettings.valueOf(request.setting);
    boolean currentSetting = false;
    String sql = " where id = :id";
    switch (settings) {
    case NOTIFICATION:
      currentSetting = settingResponse.enableNotification;
      sql = "enableNotification = :setting " + sql + " and enableNotification = :currentSetting";
      break;
    case MUSIC_THEME:
      currentSetting = settingResponse.enableMusicTheme;
      sql = "enableMusicTheme = :setting " + sql + " and enableMusicTheme = :currentSetting";
      break;
    case SOUND_EFFECT:
      currentSetting = settingResponse.enableSoundEffect;
      sql = "enableSoundEffect = :setting " + sql + " and enableSoundEffect = :currentSetting";
      break;
    }
    if (request.enable == currentSetting) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("id", userId);
    params.put("setting", request.enable);
    params.put("currentSetting", currentSetting);
    if (User.updateUser(sql, params)) {
      redisService.clearCacheByPrefix("SETTING_USER_" + userId);
      return true;
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }

  public void updateLevelByExp(long userId) {
    User user = User.findById(userId);
    Long maxLevel = redisService.findMaxLevel();
    Level level = Level.getLevelBeExp(user.xp);
    if (null != level && level.id - user.level.id > 0 && level.id < maxLevel) {
      User.updateLevelAndPointSkill(userId, level.id, new BigDecimal(level.id - user.level.id));
    }
  }

  public long maxDeviceUserByLevel(long levelId) {
    if (levelId < 5) {
      return 1;
    } else if (levelId < 15) {
      return 2;
    } else {
      return 3;
    }
  }
}

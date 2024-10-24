package xyz.telegram.depinalliance.resources;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.vertx.core.http.HttpServerRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestHeader;
import xyz.telegram.depinalliance.common.configs.AmazonS3Config;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.*;
import xyz.telegram.depinalliance.common.models.response.*;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;
import xyz.telegram.depinalliance.services.JwtService;
import xyz.telegram.depinalliance.services.RedisService;
import xyz.telegram.depinalliance.services.TelegramService;
import xyz.telegram.depinalliance.services.UserService;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author holden on 26-Jul-2024
 */
@Path("/users")
public class UserResource extends BaseResource {

  @Inject
  TelegramService telegramService;
  @Inject
  JwtService jwtService;
  @Inject
  UserService userService;
  @Inject
  RedisService redisService;
  @Inject
  AmazonS3Config amazonS3Config;
  @Inject
  Logger logger;

  @POST
  @Path("auth")
  @PermitAll
  @Transactional
  public ResponseData auth(TelegramInitDataRequest request, HttpHeaders headers,
    @Context HttpServerRequest httpServerRequest) {
    UserTelegramResponse userTelegramResponse = telegramService.validateInitData(request.initData);
    if (userTelegramResponse == null) {
      //      logger.error("Auth fail init data " + request.initData);
      return ResponseData.error(ResponseMessageConstants.HAS_ERROR);
    }
    //    User user = User.findById(userTelegramResponse.id);
    String username = StringUtils.isBlank(userTelegramResponse.username) ?
      (StringUtils.isBlank(userTelegramResponse.firstName) ?
        (StringUtils.isBlank(userTelegramResponse.lastName) ?
          userTelegramResponse.id.toString() :
          userTelegramResponse.lastName) :
        userTelegramResponse.firstName) :
      userTelegramResponse.username;

    //    if (user == null) {
    String refCode = "";
    String league = "";
    if (StringUtils.isNotBlank(request.refCode)) {
      refCode = request.refCode;
      if (StringUtils.isNotBlank(refCode) && refCode.contains("_")) {
        String[] arrays = refCode.split("_");
        refCode = arrays[0];
        league = arrays[1];
      }
    }
    User user = userService.checkStartUser(userTelegramResponse.id, username, refCode, league,
      userTelegramResponse.isPremium);
    //    }

    Map<String, Object> params = new HashMap<>();
    params.put("id", user.id);
    params.put("lastLoginTime", Utils.getCalendar().getTimeInMillis());
    String sql = "";
    if (!username.equals(user.username)) {
      sql = "username = :username,";
      params.put("username", username);
    }
    String ip = Utils.getClientIpAddress(headers, httpServerRequest);
    params.put("ip", ip);
    User.updateUser(sql + "lastLoginTime = :lastLoginTime, ip = :ip where id = :id", params);
    Map<String, Object> res = new HashMap<>();
    res.put("currentStatus", user.status);
    res.put("accessToken", jwtService.generateToken(String.valueOf(userTelegramResponse.id), username));
    return ResponseData.ok(res);
  }

  @GET
  @Path("info")
  public ResponseData info() throws BusinessException {
    User user = getUser();
    UserInfoResponse userInfoResponse = new UserInfoResponse();
    userInfoResponse.avatar = user.avatar;
    userInfoResponse.level = user.level.id;
    userInfoResponse.miningPower = Utils.stripDecimalZeros(user.miningPowerReal);
    userInfoResponse.ratePurchase = Utils.stripDecimalZeros(user.ratePurchase);
    userInfoResponse.maximumPower = Utils.stripDecimalZeros(user.maximumPower.multiply(user.rateCapacity));
    userInfoResponse.point = Utils.stripDecimalZeros(user.point);
    userInfoResponse.pointUnClaimed = user.pointUnClaimed;
    userInfoResponse.xp = Utils.stripDecimalZeros(user.xp);
    Level userLevel = redisService.findLevelById(user.level.id);
    userInfoResponse.xpLevelFrom = Utils.stripDecimalZeros(userLevel.expFrom);
    userInfoResponse.xpLevelTo = Utils.stripDecimalZeros(userLevel.expTo);
    userInfoResponse.status = user.status;
    userInfoResponse.username = user.username;
    userInfoResponse.pointSkill = Utils.stripDecimalZeros(user.pointSkill);
    userInfoResponse.timeStartMining = user.timeStartMining;
    userInfoResponse.lastLoginTime = user.lastLoginTime;
    userInfoResponse.lastCheckin = user.lastCheckIn;
    userInfoResponse.code = user.code;
    userInfoResponse.pointEarned = user.pointEarned;
    userInfoResponse.totalDevice = user.totalDevice;
    BigDecimal rateBonus = user.rateReward.subtract(BigDecimal.ONE).multiply(new BigDecimal(100));
    userInfoResponse.rateBonusReward = new BigDecimal("5").add(rateBonus);
    userInfoResponse.pointBonus = Utils.stripDecimalZeros(user.pointBonus);
    userInfoResponse.isPremium = user.isPremium != null && user.isPremium;
    userInfoResponse.detectDevice = user.detectDevice;
    userInfoResponse.devicePlatform = user.devicePlatform;
    userInfoResponse.currentTime = Utils.getCalendar().getTimeInMillis() / 1000;
    return ResponseData.ok(userInfoResponse);
  }

  @GET
  @Path("config")
  public ResponseData config() throws BusinessException {
    User user = getUser();
    SystemConfigResponse systemConfigResponse = new SystemConfigResponse();
    systemConfigResponse.maxDevice = userService.maxDeviceUserByLevel(user.level.id);
    systemConfigResponse.pointBuyDevice = new BigDecimal(
      Objects.requireNonNull(redisService.findConfigByKey(Enums.Config.POINT_BUY_DEVICE)));
    systemConfigResponse.urlImage = amazonS3Config.awsUrl();
    return ResponseData.ok(systemConfigResponse);
  }

  @GET
  @Path("settings")
  public ResponseData settings() throws BusinessException {
    return ResponseData.ok(redisService.findSettingUserById(getTelegramId()));
  }

  @POST
  @Path("settings")
  public ResponseData settings(SettingRequest request) throws BusinessException {
    return ResponseData.ok(userService.setting(getTelegramId(), request));
  }

  @GET
  @Path("detect-device-info")
  public ResponseData detectDeviceInfo(@RestHeader("device-info") String device,
    @RestHeader("platform") String platform) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      Object res = userService.detectDeviceInfo(getUser(), device);
      //      Thread.sleep(1000);
      return ResponseData.ok(res);
    }
  }

  @POST
  @Path("detect-device-info")
  public ResponseData detectDeviceInfo1(Object request) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      Object res = userService.detectDeviceInfo1(getUser(), request);
      return ResponseData.ok(res);
    }
  }

  @GET
  @Path("claim-reward-new-user")
  public ResponseData claimRewardNewUser() throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      Object res = userService.claimRewardNewUser(getUser());
      return ResponseData.ok(res);
    }
  }

  @GET
  @Path("start-contributing")
  public ResponseData startContributing() throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      Object res = userService.startContributing(getUser());
      return ResponseData.ok(res);
    }
  }

  @GET
  @Path("mining")
  public ResponseData mining() throws Exception {
    return ResponseData.ok(userService.mining(getUser()));
  }

  @GET
  @Path("claim")
  public ResponseData claim() throws Exception {
    return ResponseData.ok(userService.claim(getUser()));
  }

  @GET
  @Path("ranking-engineer")
  public ResponseData ranking() {
    Map<String, Object> res = new HashMap<>();
    res.put("currentRank", User.findRankByUserId(getTelegramId()));
    res.put("ranking",
      User.find("id != 1 ", Sort.descending("miningPowerReal").and("createdAt", Sort.Direction.Ascending)).page(0, 30)
        .project(RankingResponse.class).list());
    return ResponseData.ok(res);
  }

  @GET
  @Path("ranking-earned")
  public ResponseData rankingEarned() {
    Map<String, Object> res = new HashMap<>();
    res.put("currentRank", User.findRankEarnedByUserId(getTelegramId()));
    res.put("ranking",
      User.find("id != 1 ", Sort.descending("pointEarned").and("miningPowerReal", Sort.Direction.Descending))
        .page(0, 30).project(RankingEarnedResponse.class).list());
    return ResponseData.ok(res);
  }

  @GET
  @Path("skills")
  public ResponseData getUserSkill() throws Exception {
    User user = getUser();
    List<UserSkillResponse> userSkills = userService.getUserSkill(user.id);
    Map<String, Object> data = new HashMap<>();
    data.put("skill", userSkills);
    data.put("pointSkill", Utils.stripDecimalZeros(user.pointSkill));
    data.put("point", Utils.stripDecimalZeros(user.point));
    return ResponseData.ok(data);
  }

  private BigDecimal getUserRate(int skillId, User user, SkillLevel skillLevel) {
    switch (skillId) {
    case 1:
      return null != user ? user.rateMining : skillLevel.rateMining;
    case 2:
      return null != user ? user.ratePurchase : skillLevel.ratePurchase;
    case 3:
      return null != user ? user.rateCountDown : skillLevel.rateCountDown;
    case 4:
      return null != user ? user.rateReward : skillLevel.rateReward;
    case 5:
      return null != user ? user.rateCapacity : skillLevel.rateCapacity;
    }
    return BigDecimal.ZERO;
  }

  @GET
  @Path("skills/{skillId}/next-level")
  public ResponseData getSkillNextLevel(@PathParam("skillId") Long skillId) throws Exception {
    User user = getUser();
    UserSkill userSkill = UserSkill.findByUserIdAndSkillId(user.id, skillId).get();
    Optional<SkillLevel> optional = SkillLevel.findBySkillAndLevel(skillId, userSkill.level + 1);
    SkillLevelNextResponse levelNextResponse = new SkillLevelNextResponse();
    if (optional.isPresent()) {
      SkillLevel skillLevel = optional.get();
      Skill skill = redisService.findSkillById(skillLevel.skill.id);
      levelNextResponse.skillId = skill.id;
      levelNextResponse.name = skill.name;
      levelNextResponse.description = skill.description;
      levelNextResponse.levelCurrent = userSkill.level;
      levelNextResponse.levelUpgrade = userSkill.level + 1;
      levelNextResponse.feeUpgrade = Utils.stripDecimalZeros(skillLevel.feeUpgrade);
      levelNextResponse.feePointUpgrade = Utils.stripDecimalZeros(SkillPoint.getPointRequire(user.id).point);
      levelNextResponse.effectCurrent = Utils.stripDecimalZeros(getUserRate(skillId.intValue(), user, null));
      levelNextResponse.rateEffect = Utils.stripDecimalZeros(getUserRate(skillId.intValue(), null, skillLevel));
      return ResponseData.ok(levelNextResponse);
    }
    return ResponseData.ok(null);
  }

  @POST
  @Path("upgrade-skill")
  @Transactional
  public ResponseData upgradeSkill(SkillUpgradeRequest request) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(userService.upgradeSkill(getUser(), request.skillId));
    }
  }

  @GET
  @Path("avatar")
  public ResponseData listAvatar() {
    String avatarLst = redisService.findConfigByKey(Enums.Config.AVATAR_LIST);
    return ResponseData.ok(avatarLst.split(";"));
  }

  @POST
  @Path("avatar")
  @Transactional
  public ResponseData updateAvatar(AvatarRequest request) throws BusinessException {
    if (request == null || StringUtils.isBlank(request.avatar)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    String avatarLst = redisService.findConfigByKey(Enums.Config.AVATAR_LIST);
    List<String> lstAvatar = Arrays.asList(avatarLst.split(";"));
    if (!lstAvatar.contains(request.avatar)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("id", getTelegramId());
    params.put("avatar", request.avatar);
    User.updateUser("avatar = :avatar where id = :id", params);
    return ResponseData.ok(request.avatar);
  }

  @GET
  @Path("friend")
  public ResponseData friend(PagingParameters pagingParameters) {
    return ResponseData.ok(User.findFriendByUserAndPaging(pagingParameters, getTelegramId()));
  }

  @GET
  @Path("next-level")
  public ResponseData nextLevel() {
    User user = getUser();
    List<Level> levels = redisService.findNextLevel(user.level.id);
    BigDecimal maxMiningPower = BigDecimal.ZERO;
    List<LevelResponse> levelResponses = new ArrayList<>();
    for (int i = 0; i < levels.size(); i++) {
      Level level = levels.get(i);
      LevelResponse levelResponse = new LevelResponse();
      levelResponse.level = level.id;
      levelResponse.xpLevelFrom = Utils.stripDecimalZeros(level.expFrom);
      levelResponse.xpLevelTo = Utils.stripDecimalZeros(level.expTo);
      levelResponse.maxDevice = userService.maxDeviceUserByLevel(level.id);
      maxMiningPower = maxMiningPower.add(level.maxMiningPower);
      levelResponse.maximumPower = Utils.stripDecimalZeros(
        user.maximumPower.add(maxMiningPower).multiply(user.rateCapacity));
      levelResponses.add(levelResponse);
    }
    return ResponseData.ok(levelResponses);
  }

  @POST
  @Path("connect-wallet")
  @Transactional
  public ResponseData<?> connectWallet(ConnectWalletRequest request) throws Exception {
    if (request != null && StringUtils.isNotBlank(request.address) && StringUtils.isNotBlank(request.type)) {
      Parameters parameters = new Parameters();
      parameters.and("id", getTelegramId());
      parameters.and("address", request.address);
      parameters.and("connectFrom", request.connectFrom);
      String sql = "";
      if (request.type.equalsIgnoreCase("EVM")) {
        sql += "addressEvm = :address , connectByEvm = :connectFrom ";
      } else if (request.type.equalsIgnoreCase("TON")) {
        sql += "addressTon = :address , connectByTon = :connectFrom ";
      }
      sql += " where id = :id";
      User.update(sql, parameters);
    }
    return ResponseData.ok();
  }

}

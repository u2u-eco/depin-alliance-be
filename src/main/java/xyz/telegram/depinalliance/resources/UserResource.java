package xyz.telegram.depinalliance.resources;

import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.AvatarRequest;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.request.SkillUpgradeRequest;
import xyz.telegram.depinalliance.common.models.request.TelegramInitDataRequest;
import xyz.telegram.depinalliance.common.models.response.*;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.SkillLevel;
import xyz.telegram.depinalliance.entities.SystemConfig;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.services.JwtService;
import xyz.telegram.depinalliance.services.TelegramService;
import xyz.telegram.depinalliance.services.UserService;

import java.math.RoundingMode;
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

  @POST
  @Path("auth")
  @PermitAll
  @Transactional
  public ResponseData auth(TelegramInitDataRequest request) throws Exception {
    UserTelegramResponse userTelegramResponse = telegramService.validateInitData(request.initData);
    if (userTelegramResponse == null) {
      return ResponseData.error(ResponseMessageConstants.HAS_ERROR);
    }
    User user = User.findById(userTelegramResponse.id);
    if (user == null) {
      return ResponseData.error(ResponseMessageConstants.NOT_FOUND);
    }
    String username = StringUtils.isBlank(userTelegramResponse.username) ?
      (StringUtils.isBlank(userTelegramResponse.firstName) ?
        (StringUtils.isBlank(userTelegramResponse.lastName) ?
          userTelegramResponse.id.toString() :
          userTelegramResponse.lastName) :
        userTelegramResponse.firstName) :
      userTelegramResponse.username;
    Map<String, Object> params = new HashMap<>();
    params.put("id", user.id);
    params.put("lastLoginTime", Utils.getCalendar().getTimeInMillis());
    String sql = "";
    if (!username.equals(user.username)) {
      sql = "username = :username,";
      params.put("username", username);
    }

    User.updateUser(sql + "lastLoginTime = :lastLoginTime where id = :id", params);
    if (user.status == Enums.UserStatus.MINING) {
      userService.mining(user);
    }
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
    userInfoResponse.miningPower = Utils.stripDecimalZeros(user.miningPower.multiply(user.rateMining));
    userInfoResponse.maximumPower = Utils.stripDecimalZeros(user.maximumPower);
    userInfoResponse.point = user.point.setScale(0, RoundingMode.DOWN);
    userInfoResponse.pointUnClaimed = user.pointUnClaimed;
    userInfoResponse.xp = Utils.stripDecimalZeros(user.xp);
    userInfoResponse.status = user.status;
    userInfoResponse.username = user.username;
    userInfoResponse.pointSkill = user.pointSkill;
    userInfoResponse.timeStartMining = user.timeStartMining;
    userInfoResponse.lastLoginTime = user.lastLoginTime;
    userInfoResponse.lastCheckin = user.lastCheckIn;
    userInfoResponse.code = user.code;
    return ResponseData.ok(userInfoResponse);
  }

  @GET
  @Path("detect-device-info")
  public ResponseData detectDeviceInfo() throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      Object res = userService.detectDeviceInfo(getUser());
      Thread.sleep(1000);
      return ResponseData.ok(res);
    }
  }

  @GET
  @Path("claim-reward-new-user")
  public ResponseData claimRewardNewUser() throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      Object res = userService.claimRewardNewUser(getUser());
      Thread.sleep(1000);
      return ResponseData.ok(res);
    }
  }

  @GET
  @Path("start-contributing")
  public ResponseData startContributing() throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      Object res = userService.startContributing(getUser());
      Thread.sleep(1000);
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
      User.findAll(Sort.descending("miningPower").and("createdAt", Sort.Direction.Ascending)).page(0, 30)
        .project(RankingResponse.class).list());
    return ResponseData.ok(res);
  }

  @GET
  @Path("skills")
  public ResponseData getUserSkill() throws Exception {
    User user = getUser();
    List<UserSkillResponse> userSkills = userService.getUserSkill(user.id);
    Map<String, Object> data = new HashMap<>();
    data.put("skill", userSkills);
    data.put("point", user.pointSkill);
    return ResponseData.ok(data);
  }

  @GET
  @Path("skills/{skillId}/next-level")
  public ResponseData getSkillNextLevel(@PathParam("skillId") Long skillId) throws Exception {
    User user = getUser();
    Optional<SkillLevel> optional = SkillLevel.findBySkillAndLevel(skillId, user.level.id + 1);
    SkillLevelNextResponse levelNextResponse = new SkillLevelNextResponse();
    if (optional.isPresent()) {
      SkillLevel skillLevel = optional.get();
      levelNextResponse.skillId = skillLevel.skill.id;
      levelNextResponse.name = skillLevel.skill.name;
      levelNextResponse.levelCurrent = skillLevel.level;
      levelNextResponse.levelUpgrade = skillLevel.level + 1;
      levelNextResponse.feeUpgrade = skillLevel.feeUpgrade.setScale(2, RoundingMode.UP);
      levelNextResponse.rateEffect = skillLevel.rateMining.add(skillLevel.rateReward).add(skillLevel.ratePurchase)
        .setScale(2, RoundingMode.UP);
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
    String avatarLst = SystemConfig.findByKey(Enums.Config.AVATAR_LIST);
    return ResponseData.ok(avatarLst.split(";"));
  }

  @POST
  @Path("avatar")
  @Transactional
  public ResponseData updateAvatar(AvatarRequest request) throws BusinessException {
    if (request == null || StringUtils.isBlank(request.avatar)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    String avatarLst = SystemConfig.findByKey(Enums.Config.AVATAR_LIST);
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
}

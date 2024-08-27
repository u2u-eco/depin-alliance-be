package xyz.telegram.depinalliance.resources;

import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.models.request.TelegramInitDataRequest;
import xyz.telegram.depinalliance.common.models.response.RankingResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.models.response.UserInfoResponse;
import xyz.telegram.depinalliance.common.models.response.UserTelegramResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.services.JwtService;
import xyz.telegram.depinalliance.services.TelegramService;
import xyz.telegram.depinalliance.services.UserService;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

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
    Map<String, Object> params = new HashMap<>();
    params.put("id", user.id);
    params.put("lastLoginTime", Utils.getCalendar().getTimeInMillis());
    String sql = "";
    if (!userTelegramResponse.username.equals(user.username)) {
      sql = "username = :username,";
      params.put("username", userTelegramResponse.username);
    }

    User.updateUser(sql + "lastLoginTime = :lastLoginTime where id = :id", params);
    if (user.status == Enums.UserStatus.MINING) {
      userService.mining(user);
    }
    Map<String, Object> res = new HashMap<>();
    res.put("currentStatus", user.status);
    res.put("accessToken",
      jwtService.generateToken(String.valueOf(userTelegramResponse.id), userTelegramResponse.username));
    return ResponseData.ok(res);
  }

  @GET
  @Path("info")
  public ResponseData info() {
    User user = getUser();
    UserInfoResponse userInfoResponse = new UserInfoResponse();
    userInfoResponse.avatar = user.avatar;
    userInfoResponse.level = user.level.id;
    userInfoResponse.miningPower = Utils.stripDecimalZeros(user.miningPower);
    userInfoResponse.maximumPower = Utils.stripDecimalZeros(user.maximumPower);
    userInfoResponse.point = user.point.setScale(0, RoundingMode.DOWN);
    userInfoResponse.pointUnClaimed = user.pointUnClaimed.setScale(0, RoundingMode.DOWN);
    userInfoResponse.xp = Utils.stripDecimalZeros(user.xp);
    userInfoResponse.status = user.status;
    userInfoResponse.username = user.username;
    userInfoResponse.timeStartMining = user.timeStartMining;
    userInfoResponse.lastLoginTime = user.lastLoginTime;
    userInfoResponse.lastCheckin = user.lastCheckIn;
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
  public ResponseData ranking() throws Exception {
    Map<String, Object> res = new HashMap<>();
    res.put("currentRank", User.findRankByUserId(getTelegramId()));
    res.put("ranking", User.findAll(Sort.descending("miningPower").and("createdAt", Sort.Direction.Ascending)).page(0, 30).project(RankingResponse.class).list());
    return ResponseData.ok(res);
  }
}

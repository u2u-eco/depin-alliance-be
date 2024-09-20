package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.configs.AmazonS3Config;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.LeagueRequest;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author holden on 28-Aug-2024
 */
@ApplicationScoped
public class LeagueService {
  public static final List<String> IMAGE_CONTENT_TYPE = new ArrayList<>(
    Arrays.asList("image/jpg", "image/jpeg", "image/png"));

  @Inject
  S3Service s3Service;
  @Inject
  RedisService redisService;
  @Inject
  AmazonS3Config amazonS3Config;

  @Transactional
  public LeagueResponse createLeague(User user, LeagueRequest request) throws Exception {
    if (user.league != null || request == null || request.file == null || !s3Service.validateFileType(request.file,
      IMAGE_CONTENT_TYPE) || StringUtils.isBlank(request.name)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (!validateName(request)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    String urlImage = s3Service.uploadFile(Enums.FolderImage.LEAGUE.getFolder(), UUID.randomUUID().toString(),
      request.file);
    long currentTime = Utils.getCalendar().getTimeInMillis();
    League league = new League();
    league.name = request.name;
    league.user = user;
    league.avatar = amazonS3Config.awsUrl() + urlImage;
    league.totalContributors = 1;
    league.totalMining = user.miningPower.multiply(user.rateMining);
    league.level = new LeagueLevel(1L);
    League.createLeague(league);
    Map<String, Object> params = new HashMap<>();
    params.put("league", league.id);
    params.put("id", user.id);
    params.put("joinedLeagueAt", currentTime);
    if (!User.updateUser("league.id = :league, joinedLeagueAt = :joinedLeagueAt where id = :id and league is null",
      params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }

    Map<String, Object> paramsCancel = new HashMap<>();
    paramsCancel.put("status", Enums.LeagueJoinRequestStatus.CANCELLED);
    paramsCancel.put("statusStr", Enums.LeagueJoinRequestStatus.CANCELLED.name());
    paramsCancel.put("userId", user.id);
    paramsCancel.put("userAction", user.id);
    paramsCancel.put("updatedAt", currentTime);
    paramsCancel.put("statusOld", Enums.LeagueJoinRequestStatus.PENDING);
    LeagueJoinRequest.update(
      "status = :status, hash = CONCAT(:userId,'_',league.id,'_',:statusStr,'_',:updatedAt), updatedAt = :updatedAt, userAction.id = :userAction where user.id = :userId and status = :statusOld",
      paramsCancel);

    LeagueMemberHistory.create(league, user, user, Enums.LeagueMemberType.CREATE);
    return new LeagueResponse(league, user.code);
  }

  @Transactional
  public LeagueResponse joinLeague(User user, String code) throws BusinessException {
    if (user.league != null || StringUtils.isBlank(code)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League league = redisService.findLeagueByCode(code, true);
    if (league == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    LeagueJoinRequest requestCheck = LeagueJoinRequest.findPendingByUserAndLeague(user.id, league.id);
    if (requestCheck != null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    LeagueJoinRequest request = new LeagueJoinRequest();
    request.league = league;
    request.user = user;
    request.status = Enums.LeagueJoinRequestStatus.PENDING;
    request.hash = user.id + "_" + league.id + "_" + request.status;
    request.create();
    request.persist();
    return new LeagueResponse(league, user.code);
  }

  @Transactional
  public boolean leaveLeague(User user) throws BusinessException {
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    user.league = redisService.findLeagueById(user.league.id, true);
    if (Objects.equals(user.league.user.id, user.id)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("league", null);
    params.put("joinedLeagueAt", null);
    params.put("id", user.id);
    if (!User.updateUser("league.id = :league, joinedLeagueAt = :joinedLeagueAt where id = :id", params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    LeagueMemberHistory.create(user.league, user, user, Enums.LeagueMemberType.LEAVE);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", user.league.id);
    //    leagueParams.put("totalMining", user.miningPower.multiply(user.rateMining));
    League.updateObject("totalContributors = totalContributors - 1" +
      //        ", totalMining = totalMining - :totalMining " +
      " where id = :id", leagueParams);
    return true;
  }

  @Transactional
  public boolean kick(User user, long id) throws BusinessException {
    User userKick = User.findById(id);
    if (userKick == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    if (user.league == null || userKick.league == null || user.id == id || userKick.league.id != user.league.id) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    user.league = redisService.findLeagueById(user.league.id, true);
    if (!Objects.equals(user.league.user.id, user.id)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("league", null);
    params.put("joinedLeagueAt", null);
    params.put("id", id);
    if (!User.updateUser("league.id = :league, joinedLeagueAt = :joinedLeagueAt where id = :id", params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    LeagueMemberHistory.create(user.league, userKick, user, Enums.LeagueMemberType.KICK);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", user.league.id);
    //    leagueParams.put("totalMining", userKick.miningPower.multiply(userKick.rateMining));
    League.updateObject("totalContributors = totalContributors - 1" +
      //        ", totalMining = totalMining - :totalMining" +
      " where id = :id", leagueParams);
    return true;
  }

  @Transactional
  public boolean cancel(User user, String code) throws BusinessException {
    if (user.league != null) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_REQUEST_INVALID);
    }
    League league = redisService.findLeagueByCode(code, true);
    if (league == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    LeagueJoinRequest requestCheck = LeagueJoinRequest.findPendingByUserAndLeague(user.id, league.id);
    if (requestCheck == null) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_REQUEST_INVALID);
    }
    long currentTime = Utils.getCalendar().getTimeInMillis();
    Map<String, Object> params = new HashMap<>();
    params.put("status", Enums.LeagueJoinRequestStatus.CANCELLED);
    params.put("hash", user.id + "_" + Enums.LeagueJoinRequestStatus.CANCELLED + "_" + currentTime);
    params.put("id", requestCheck.id);
    params.put("updatedAt", currentTime);
    params.put("userAction", user);
    params.put("statusOld", Enums.LeagueJoinRequestStatus.PENDING);
    LeagueJoinRequest.update(
      "status = :status, hash = :hash, updatedAt = :updatedAt, userAction = :userAction where id = :id and status = :statusOld",
      params);
    return true;
  }

  @Transactional
  public boolean reject(User ownerUser, long userId) throws BusinessException {
    if (ownerUser.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League league = redisService.findLeagueById(ownerUser.league.id, true);
    if (!Objects.equals(ownerUser.id, league.user.id)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    LeagueJoinRequest request = LeagueJoinRequest.findPendingByUserAndLeague(userId, league.id);
    if (request == null) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_REQUEST_INVALID);
    }
    long currentTime = Utils.getCalendar().getTimeInMillis();
    Map<String, Object> params = new HashMap<>();
    params.put("status", Enums.LeagueJoinRequestStatus.REJECTED);
    params.put("hash", userId + "_" + Enums.LeagueJoinRequestStatus.REJECTED + "_" + currentTime);
    params.put("id", request.id);
    params.put("userAction", ownerUser);
    params.put("updatedAt", currentTime);
    params.put("statusOld", Enums.LeagueJoinRequestStatus.PENDING);
    LeagueJoinRequest.update(
      "status = :status, hash = :hash, updatedAt = :updatedAt, userAction = :userAction where id = :id and status = :statusOld",
      params);
    return true;
  }

  @Transactional
  public LeagueResponse approve(User ownerUser, long userId) throws BusinessException {
    if (ownerUser.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League league = League.findById(ownerUser.league.id);
    if (!Objects.equals(ownerUser.id, league.user.id)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    LeagueJoinRequest request = LeagueJoinRequest.findPendingByUserAndLeague(userId, league.id);
    if (request == null) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_REQUEST_INVALID);
    }

    long currentTime = Utils.getCalendar().getTimeInMillis();
    Map<String, Object> params = new HashMap<>();
    params.put("status", Enums.LeagueJoinRequestStatus.APPROVED);
    params.put("hash", userId + "_" + league.id + "_" + Enums.LeagueJoinRequestStatus.APPROVED + "_" + currentTime);
    params.put("id", request.id);
    params.put("userAction", ownerUser);
    params.put("updatedAt", currentTime);
    params.put("statusOld", Enums.LeagueJoinRequestStatus.PENDING);
    if (LeagueJoinRequest.update(
      "status = :status, hash = :hash, updatedAt = :updatedAt, userAction = :userAction where id = :id and status = :statusOld",
      params) <= 0) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_REQUEST_INVALID);
    }
    Map<String, Object> paramsCancel = new HashMap<>();
    paramsCancel.put("status", Enums.LeagueJoinRequestStatus.CANCELLED);
    paramsCancel.put("statusStr", Enums.LeagueJoinRequestStatus.CANCELLED.name());
    //    paramsCancel.put("hash", userId + "_" + league.id + "_" + request.status + "_" + currentTime);
    paramsCancel.put("userId", userId);
    paramsCancel.put("userAction", userId);
    paramsCancel.put("updatedAt", currentTime);
    paramsCancel.put("statusOld", Enums.LeagueJoinRequestStatus.PENDING);
    LeagueJoinRequest.update(
      "status = :status, hash = CONCAT(:userId,'_',league.id,'_',:statusStr,'_',:updatedAt), updatedAt = :updatedAt, userAction.id = :userAction where user.id = :userId and status = :statusOld",
      paramsCancel);


    User user = User.findById(userId);
    Map<String, Object> paramsLeague = new HashMap<>();
    paramsLeague.put("league", league.id);
    paramsLeague.put("id", userId);
    paramsLeague.put("joinedLeagueAt", currentTime);
    if (!User.updateUser("league.id = :league, joinedLeagueAt = :joinedLeagueAt where id = :id and league is null", paramsLeague)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_REQUEST_INVALID);
    }
    LeagueMemberHistory.create(league, user, ownerUser, Enums.LeagueMemberType.JOIN);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", league.id);
    //    leagueParams.put("totalMining", user.miningPower.multiply(user.rateMining));
    League.updateObject("totalContributors = totalContributors + 1" +
      //        ", totalMining = totalMining + :totalMining " +
      " where id = :id", leagueParams);
    return new LeagueResponse(league, user.code);
  }

  public void updateXp(User user, BigDecimal xp) {
    if (user.league == null || xp == null || xp.compareTo(BigDecimal.ZERO) <= 0) {
      return;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("xp", xp);
    params.put("id", user.league.id);
    League.updateObject(
      "xp = xp + :xp, level.id = (Select id from LeagueLevel where xp +:xp between expFrom and expTo) where id = :id",
      params);

    //    Long maxLevel = LeagueLevel.maxLevel();
    //    LeagueLevel level = LeagueLevel.getLevelBeExp(user.league.id);
    //    if (null != level && level.id - user.level.id > 0 && level.id < maxLevel) {
    //      League.updateLevel(user.league.id, level.id);
    //    }
  }

  public boolean validateName(LeagueRequest request) {
    if (request == null || StringUtils.isBlank(request.name) || request.name.trim().length() < 3) {
      return false;
    }
    return League.countByNameNormalize(request.name) <= 0;
  }
}

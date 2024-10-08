package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.configs.AmazonS3Config;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.ContributeItemRequest;
import xyz.telegram.depinalliance.common.models.request.FundRequest;
import xyz.telegram.depinalliance.common.models.request.LeagueRequest;
import xyz.telegram.depinalliance.common.models.request.LeagueRoleRequest;
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
    List<String> roles = Arrays.asList(Enums.LeagueRole.ADMIN_KICK.name(), Enums.LeagueRole.ADMIN_REQUEST.name());
    LeagueMember.create(league, user, true, String.join(";", roles));
    LeagueMemberHistory.create(league, user, user, Enums.LeagueMemberType.CREATE);
    return new LeagueResponse(league, user.code);
  }

  @Transactional
  public LeagueResponse changeAvatar(User user, LeagueRequest request) throws Exception {
    if (user.league == null || request == null || request.file == null || !s3Service.validateFileType(request.file,
      IMAGE_CONTENT_TYPE)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    user.league = redisService.findLeagueById(user.league.id, true);
    if (!Objects.equals(user.league.user.id, user.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_MEMBER_NOT_EXIST);
    }
    String urlImage = s3Service.uploadFile(Enums.FolderImage.LEAGUE.getFolder(), UUID.randomUUID().toString(),
      request.file);
    user.league.avatar = amazonS3Config.awsUrl() + urlImage;
    Map<String, Object> params = new HashMap<>();
    params.put("avatar", user.league.avatar);
    params.put("id", user.league.id);
    params.put("updatedAt", System.currentTimeMillis());
    if (League.updateObject("avatar = :avatar, updatedAt = :updatedAt where id = :id", params)) {
      return new LeagueResponse(user.league, user.code);
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
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
  public boolean leaveLeague(long userId) throws BusinessException {
    LeagueMember member = redisService.findLeagueMemberByUserId(userId);
    if (member == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (member.isAdmin) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("league", null);
    params.put("joinedLeagueAt", null);
    params.put("id", member.user.id);
    if (!User.updateUser("league.id = :league, joinedLeagueAt = :joinedLeagueAt where id = :id", params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    LeagueMemberHistory.create(member.league, member.user, member.user, Enums.LeagueMemberType.LEAVE);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", member.league.id);
    leagueParams.put("userId", userId);
    if (!League.updateObject("totalContributors = totalContributors - 1 where id = :id and user.id != :userId",
      leagueParams)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    member.delete();
    clearCacheLeagueMember(userId);
    if (StringUtils.isNotBlank(member.leagueRole)) {
      clearCacheAdmin(member.league.id);
    }
    return true;
  }

  @Transactional
  public boolean leaveLeagueAdmin(long userId, long assignAdmin) throws BusinessException {
    if (userId == assignAdmin) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    LeagueMember member = redisService.findLeagueMemberByUserId(userId);
    if (member == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (!member.isAdmin) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League league = League.findById(member.league.id);
    long countMember = LeagueMember.count("league.id", member.league.id);
    LeagueMember userAssign = null;
    if (countMember > 1) {
      if (assignAdmin <= 0) {
        throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
      } else {
        userAssign = redisService.findLeagueMemberByUserId(assignAdmin);
        if (userAssign == null || !Objects.equals(userAssign.league.id, member.league.id)) {
          throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
        }
      }
    }

    Map<String, Object> params = new HashMap<>();
    params.put("league", null);
    params.put("joinedLeagueAt", null);
    params.put("id", member.user.id);
    params.put("leagueId", league.id);
    if (!User.updateUser(
      "league.id = :league, joinedLeagueAt = :joinedLeagueAt where id = :id and league.id = :leagueId", params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    LeagueMemberHistory.create(member.league, member.user, member.user, Enums.LeagueMemberType.LEAVE);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", member.league.id);
    if (userAssign != null) {
      leagueParams.put("admin", userAssign.user.id);
      if (!League.updateObject("totalContributors = totalContributors - 1, user.id = :admin where id = :id",
        leagueParams)) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
      LeagueMemberHistory.create(member.league, userAssign.user, member.user, Enums.LeagueMemberType.CHANGE_ADMIN);
      clearCacheLeagueMember(userAssign.user.id);
      List<String> roles = Arrays.asList(Enums.LeagueRole.ADMIN_KICK.name(), Enums.LeagueRole.ADMIN_REQUEST.name());
      String sql = " isAdmin = true, leagueRole = :leagueRole where user.id = :id ";
      Map<String, Object> paramsRole = new HashMap<>();
      paramsRole.put("id", userAssign.user.id);
      paramsRole.put("leagueRole", String.join(";", roles));
      if (!LeagueMember.updateMember(sql, paramsRole)) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
    } else {
      if (!League.updateObject(
        "totalContributors = 0, user = null, isActive = false where id = :id and totalContributors = 1",
        leagueParams)) {
        throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
      }
    }

    member.delete();
    clearCacheLeagueMember(userId);
    clearCacheAdmin(member.league.id);
    clearCacheLeague(member.league.id, league.code);
    return true;
  }

  @Transactional
  public boolean kick(User user, long id) throws BusinessException {
    LeagueMember userKick = redisService.findLeagueMemberByUserId(id);
    if (userKick == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    if (user.league == null || user.id == id || !Objects.equals(userKick.league.id, user.league.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_MEMBER_NOT_EXIST);
    }
    user.league = redisService.findLeagueById(user.league.id, true);
    List<Long> admins = redisService.findListAdminLeagueByRoleAndLeague(user.league.id, Enums.LeagueRole.ADMIN_KICK);
    if (!admins.contains(user.id) || id == user.league.user.id || (StringUtils.isNotBlank(
      userKick.leagueRole) && !Objects.equals(user.league.user.id, user.id))) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("league", null);
    params.put("joinedLeagueAt", null);
    params.put("id", id);
    if (!User.updateUser("league.id = :league, joinedLeagueAt = :joinedLeagueAt where id = :id", params)) {
      throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
    }
    LeagueMemberHistory.create(user.league, userKick.user, user, Enums.LeagueMemberType.KICK);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", user.league.id);
    League.updateObject("totalContributors = totalContributors - 1 where id = :id", leagueParams);
    userKick.delete();
    clearCacheLeagueMember(id);
    if (StringUtils.isNotBlank(userKick.leagueRole)) {
      clearCacheAdmin(user.league.id);
    }
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
    List<Long> admins = redisService.findListAdminLeagueByRoleAndLeague(ownerUser.league.id,
      Enums.LeagueRole.ADMIN_REQUEST);
    if (!admins.contains(ownerUser.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
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
    List<Long> admins = redisService.findListAdminLeagueByRoleAndLeague(ownerUser.league.id,
      Enums.LeagueRole.ADMIN_REQUEST);
    if (!admins.contains(ownerUser.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
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
    if (!User.updateUser("league.id = :league, joinedLeagueAt = :joinedLeagueAt where id = :id and league is null",
      paramsLeague)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_REQUEST_INVALID);
    }
    LeagueMember.create(league, user);
    LeagueMemberHistory.create(league, user, ownerUser, Enums.LeagueMemberType.JOIN);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", league.id);
    League.updateObject("totalContributors = totalContributors + 1 where id = :id", leagueParams);
    return new LeagueResponse(league, user.code);
  }

  @Transactional
  public boolean fund(User user, FundRequest request) {
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_MEMBER_NOT_EXIST);
    }
    if (request == null || !Utils.validateAmountBigDecimal(request.amount) || user.point.compareTo(
      request.amount) < 0) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (User.updatePointUser(user.id, request.amount.multiply(new BigDecimal("-1"))) && League.updatePoint(
      user.league.id, request.amount) && LeagueMember.updatePointFundingLeague(user.id, request.amount)) {
      LeagueFundHistory fundHistory = new LeagueFundHistory();
      fundHistory.league = user.league;
      fundHistory.user = user;
      fundHistory.point = request.amount;
      fundHistory.create();
      fundHistory.persist();
      clearCacheLeagueMember(user.id);
      return true;
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }

  @Transactional
  public boolean contribute(User user, ContributeItemRequest request) {
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_MEMBER_NOT_EXIST);
    }
    if (request == null || request.number <= 0 || StringUtils.isBlank(request.code)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Item item = redisService.findItemByCode(request.code);
    if (item == null || item.type == Enums.ItemType.SPECIAL) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    List<Long> itemIds = UserItem.findItemNotHasDevice(user.id, item.id, request.number);
    if (itemIds == null || itemIds.isEmpty() || itemIds.size() < request.number) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ITEM_CONTRIBUTE_NOT_ENOUGH);
    }
    String sql = "isActive = false where id in (:ids) and user.id = :userId and isActive = true and userDevice is null";
    Map<String, Object> params = new HashMap<>();
    params.put("ids", itemIds);
    params.put("userId", user.id);
    if (UserItem.updateObject(sql, params) == itemIds.size()) {
      BigDecimal sum = UserItem.sumMiningPowerByIds(itemIds);
      if (League.updateProfit(user.league.id, sum) && LeagueMember.updateLeagueContributeProfit(user.id, sum)) {
        for (Long id : itemIds) {
          LeagueContributeHistory history = new LeagueContributeHistory();
          history.league = user.league;
          history.user = user;
          history.userItem = new UserItem(id);
          history.persist();
          history.create();
          history.persist();
        }
      }
      clearCacheLeagueMember(user.id);
      return true;
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }

  @Transactional
  public boolean updateRole(User ownerUser, LeagueRoleRequest request) {
    if (ownerUser.league == null || request == null || request.userId <= 0 || ownerUser.id == request.userId || StringUtils.isBlank(
      request.role)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Enums.LeagueRole role = Enums.LeagueRole.valueOf(request.role.toUpperCase());
    League league = redisService.findLeagueById(ownerUser.league.id, true);
    if (!Objects.equals(ownerUser.id, league.user.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
    }
    LeagueMember member = redisService.findLeagueMemberByUserId(request.userId);
    if (member == null || !member.league.id.equals(league.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_MEMBER_NOT_EXIST);
    }
    Set<String> roles = new HashSet<>();
    if (StringUtils.isNotBlank(member.leagueRole)) {
      roles = new HashSet<>(Arrays.asList(member.leagueRole.split(";")));
    }
    if (request.isActive && roles.contains(request.role)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
    } else if (!request.isActive && !roles.contains(request.role)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
    }
    String sql = " leagueRole = :leagueRole where user.id = :id ";
    Map<String, Object> params = new HashMap<>();

    params.put("id", member.user.id);
    if (request.isActive) {
      roles.add(request.role);
    } else {
      roles.remove(request.role);
    }
    params.put("leagueRole", String.join(";", roles));
    if (LeagueMember.updateMember(sql, params)) {
      Enums.LeagueMemberType memberType;
      memberType = switch (role) {
        case ADMIN_REQUEST -> request.isActive ?
          Enums.LeagueMemberType.TURN_ON_ADMIN_REQUEST :
          Enums.LeagueMemberType.TURN_OFF_ADMIN_REQUEST;
        case ADMIN_KICK ->
          request.isActive ? Enums.LeagueMemberType.TURN_ON_ADMIN_KICK : Enums.LeagueMemberType.TURN_OFF_ADMIN_KICK;
      };

      LeagueMemberHistory.create(league, member.user, ownerUser, memberType);
      clearCacheAdmin(member.league.id);
      clearCacheLeagueMember(member.user.id);
      return true;
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
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

  public void clearCacheAdmin(long leagueId) {
    redisService.clearCacheByPrefix("ADMIN_LEAGUE_" + leagueId);
  }

  public void clearCacheLeagueMember(long userId) {
    redisService.clearCacheByPrefix("LEAGUE_MEMBER_" + userId);
  }

  public void clearCacheLeague(long leagueId, String code) {
    redisService.clearCacheByPrefix("LEAGUE_ID_" + leagueId);
    redisService.clearCacheByPrefix("LEAGUE_CODE_" + code);
  }

  public boolean validateName(LeagueRequest request) {
    if (request == null || StringUtils.isBlank(request.name) || request.name.trim().length() < 3) {
      return false;
    }
    return League.countByNameNormalize(request.name) <= 0;
  }
}

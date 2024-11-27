package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.*;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author holden on 26-Aug-2024
 */
@ApplicationScoped
public class MissionService {

  @Inject
  UserService userService;
  @Inject
  LeagueService leagueService;
  @Inject
  TelegramService telegramService;
  @Inject
  RedisService redisService;
  @RestClient
  MiniTonClient miniTonClient;
  @Inject
  TwitterService twitterService;

  public List<DailyCheckinResponse> getListOfDailyCheckin(User user) {
    List<DailyCheckin> dailyCheckins;
    Calendar calendar = Utils.getNewDay();
    long today = calendar.getTimeInMillis() / 1000;
    long dayCheckin = user.startCheckIn == 0 ? 1 : ((today - user.startCheckIn) / 86400);
    if (user.startCheckIn == 0 || today - user.lastCheckIn > 86400 || dayCheckin < 4) {
      dailyCheckins = redisService.findFirstCheckin();
    } else {
      dailyCheckins = redisService.findListDailyCheckinByDay(dayCheckin);
    }
    if (user.lastCheckIn == today) {
      if (dayCheckin >= 4) {
        calendar.add(Calendar.DATE, -3);
      } else {
        calendar.setTimeInMillis(user.startCheckIn * 1000);
      }
      return dailyCheckins.stream().map(dailyCheckin -> {
        long time = calendar.getTimeInMillis() / 1000;
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name, time,
          dailyCheckin.point, dailyCheckin.xp, time <= today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dailyCheckinResponse;
      }).collect(Collectors.toList());
    }

    if (today - user.lastCheckIn == 86400) {
      if (dayCheckin >= 4) {
        calendar.add(Calendar.DATE, -3);
      } else {
        calendar.setTimeInMillis(user.startCheckIn * 1000);
      }
      return dailyCheckins.stream().map(dailyCheckin -> {
        long time = calendar.getTimeInMillis() / 1000;
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name, time,
          dailyCheckin.point, dailyCheckin.xp, time < today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dailyCheckinResponse;
      }).collect(Collectors.toList());
    }

    if (user.startCheckIn == 0 || today - user.lastCheckIn > 86400) {
      return dailyCheckins.stream().map(dailyCheckin -> {
        DailyCheckinResponse dailyCheckinResponse = new DailyCheckinResponse(dailyCheckin.name,
          calendar.getTimeInMillis() / 1000, dailyCheckin.point, dailyCheckin.xp, false);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return dailyCheckinResponse;
      }).collect(Collectors.toList());
    }
    return null;
  }

  @Transactional
  public BigDecimal checkin(User user) throws BusinessException {
    Calendar calendar = Utils.getNewDay();
    long today = calendar.getTimeInMillis() / 1000;
    if (user.lastCheckIn == today) {
      throw new BusinessException(ResponseMessageConstants.MISSION_CANNOT_CHECKIN_TODAY);
    }
    //chua checkin or miss
    if (user.startCheckIn == 0 || today - user.lastCheckIn > 86400) {
      DailyCheckin dailyCheckin = redisService.findDailyCheckinByDay(1);
      Map<String, Object> paramsUser = new HashMap<>();
      paramsUser.put("id", user.id);
      paramsUser.put("startCheckIn", today);
      paramsUser.put("lastCheckIn", today);
      paramsUser.put("point", dailyCheckin.point);
      paramsUser.put("xp", dailyCheckin.xp);
      paramsUser.put("pointEarned1", Utils.pointAirdrop(dailyCheckin.point));
      User.updateUser(
        "startCheckIn = :startCheckIn, lastCheckIn = :lastCheckIn, point = point + :point, pointEarned = pointEarned + :point, pointEarned1 = pointEarned1 + :pointEarned1, xp = xp + :xp where id = :id",
        paramsUser);
      if (dailyCheckin.xp != null && dailyCheckin.xp.compareTo(BigDecimal.ZERO) > 0) {
        userService.updateLevelByExp(user.id);
        leagueService.updateXp(user, dailyCheckin.xp);
      }
      return Utils.stripDecimalZeros(dailyCheckin.point);
    }
    //checkin lien tiep
    if (today - user.lastCheckIn == 86400) {
      long day = ((today - user.startCheckIn) / 86400) + 1;
      long countDays = redisService.findDailyCheckinCount();
      String sql = "";
      DailyCheckin dailyCheckin;
      Map<String, Object> paramsUser = new HashMap<>();
      if (day == (countDays + 1)) {
        dailyCheckin = redisService.findDailyCheckinByDay(1);
        paramsUser.put("startCheckIn", today);
        sql += "startCheckIn = :startCheckIn,";
      } else {
        dailyCheckin = redisService.findDailyCheckinByDay(day);
      }

      paramsUser.put("id", user.id);
      paramsUser.put("lastCheckIn", today);
      paramsUser.put("point", dailyCheckin.point);
      paramsUser.put("xp", dailyCheckin.xp);
      paramsUser.put("pointEarned1",Utils.pointAirdrop(dailyCheckin.point));
      User.updateUser(
        sql + "lastCheckIn = :lastCheckIn, point = point + :point, pointEarned = pointEarned + :point, pointEarned1 = pointEarned1 + :pointEarned1, xp = xp + :xp where id = :id",
        paramsUser);
      if (dailyCheckin.xp != null && dailyCheckin.xp.compareTo(BigDecimal.ZERO) > 0) {
        userService.updateLevelByExp(user.id);
        leagueService.updateXp(user, dailyCheckin.xp);
      }
      return Utils.stripDecimalZeros(dailyCheckin.point);
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }

  @Transactional
  public String verify(User user, long missionId, List<QuizResponse> answerArrays) {
    UserMissionResponse check = Mission.findByUserIdAndMissionId(user.id, missionId);
    if (check == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (check.status != null && check.status != Enums.MissionStatus.NOT_VERIFIED) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    boolean isChecked = false;
    if (check.isFake) {
      isChecked = true;
    } else {
      switch (check.type) {
      case QUIZ:
        if (answerArrays == null || answerArrays.isEmpty()) {
          throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
        }
        isChecked = true;
        try {
          check.quizArrays.forEach(quiz -> {
            QuizResponse quizRequest = answerArrays.stream().filter(quizAnswer -> quizAnswer.index == quiz.index)
              .findFirst().orElse(null);
            if (quizRequest == null) {
              throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
            }
            quiz.answers.forEach(answer -> {
              QuizResponse.Answer answerRequest = quizRequest.answers.stream()
                .filter(quizAnswer -> quizAnswer.index == answer.index).findFirst().orElse(null);
              assert answerRequest != null;
              if (answerRequest.isCorrect() != answer.isCorrect()) {
                throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
              }
            });
          });
        } catch (Exception e) {
          return "false";
        }
        break;
      case TELEGRAM:
        isChecked = telegramService.verifyJoinChannel(check.referId, user.id.toString());
        break;
      case FOLLOW_TWITTER:
      case RETWEETS:
      case TWEET_REPLIES:
        UserSocial userSocial = redisService.findUserSocial(user.id);
        if (userSocial == null || StringUtils.isBlank(userSocial.twitterUsername)) {
          throw new BusinessException(ResponseMessageConstants.USER_MUST_LINK_TWITTER);
        }
        if (check.userMissionId != null && check.userMissionId > 0) {
          Map<String, Object> params = new HashMap<>();
          params.put("id", check.userMissionId);
          params.put("status", Enums.MissionStatus.VERIFYING);
          params.put("updatedAt", Utils.getCalendar().getTimeInMillis());
          UserMission.updateObject("status = :status, updatedAt = :updatedAt where id = :id and status is null",
            params);
        } else {
          UserMission userMission = new UserMission();
          userMission.mission = new Mission(check.id);
          userMission.user = user;
          userMission.status = Enums.MissionStatus.VERIFYING;
          UserMission.create(userMission);
        }
        if (check.partnerId != null) {
          Partner.updateParticipants(check.partnerId);
          redisService.clearMissionUser("PARTNER", user.id);
        } else {
          if (check.type == Enums.MissionType.ON_TIME_IN_APP) {
            redisService.clearMissionUser("REWARD_ONE_TIME", user.id);
          } else {
            redisService.clearMissionUser("REWARD", user.id);
          }
        }
        return "verifying";
      //        isChecked = switch (check.type) {
      //          case TWITTER -> twitterService.isUserFollowing(String.valueOf(userSocial.twitterUid), check.referId);
      //          case RETWEETS -> twitterService.isUserRetweets(String.valueOf(userSocial.twitterUid), check.referId);
      //          case TWEET_REPLIES -> twitterService.isUserReplies(String.valueOf(userSocial.twitterUid), check.referId);
      //          default -> false;
      //        };
      case PLAY_MINI_TON:
        try {
          isChecked = miniTonClient.verify(user.id);
        } catch (Exception e) {
        }
        break;
      case ON_TIME_IN_APP:
        switch (check.missionRequire) {
        case CLAIM_FIRST_10000_POINT:
          if (user.pointClaimed.compareTo(new BigDecimal("10000")) >= 0) {
            isChecked = true;
          }
          break;
        case BUY_ANY_DEVICE:
          if (UserItemTradeHistory.countBuy(user.id) > 0 || user.totalDevice > 1) {
            isChecked = true;
          }
          break;
        case LEARN_ANY_SKILL:
          if (HistoryUpgradeSkill.countUpgradeSkillByUserId(user.id) > 0) {
            isChecked = true;
          }
          break;
        default:
          if (check.missionRequire.name().startsWith("INVITE_")) {
            long numberRequire = Long.parseLong(check.missionRequire.name().replace("INVITE_", ""));
            if (user.totalFriend >= numberRequire) {
              isChecked = true;
            }
          } else if (check.missionRequire.name().startsWith("LEVEL_")) {
            long numberRequire = Long.parseLong(check.missionRequire.name().replace("LEVEL_", ""));
            if (user.level.id >= numberRequire) {
              isChecked = true;
            }
          } else if (check.missionRequire.name().startsWith("EVENT_INVITE_")) {
            long numberRequire = Long.parseLong(check.missionRequire.name().replace("EVENT_INVITE_", ""));
            if (user.totalFriend >= numberRequire) {
              isChecked = true;
            }
          }
          break;
        }
        break;
      }
    }
    if (isChecked) {
      UserMission userMission = new UserMission();
      userMission.mission = new Mission(check.id);
      userMission.user = user;
      userMission.status = Enums.MissionStatus.VERIFIED;
      UserMission.create(userMission);
      if (check.partnerId != null) {
        Partner.updateParticipants(check.partnerId);
        redisService.clearMissionUser("PARTNER", user.id);
      } else {
        if (check.type == Enums.MissionType.ON_TIME_IN_APP) {
          redisService.clearMissionUser("REWARD_ONE_TIME", user.id);
        } else {
          redisService.clearMissionUser("REWARD", user.id);
        }
      }
      return "true";
    }
    return "false";
  }

  @Transactional
  public MissionRewardResponse claim(User user, long missionId) throws BusinessException {
    UserMissionResponse check = Mission.findByUserIdAndMissionId(user.id, missionId);
    if (check == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    } else if (check.status != Enums.MissionStatus.VERIFIED) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("missionId", missionId);
    params.put("userId", user.id);
    params.put("status", Enums.MissionStatus.CLAIMED);
    if (UserMission.updateObject("status = :status where user.id = :userId and mission.id = :missionId", params)) {
      User.updatePointAndXpUser(user.id, check.point, check.xp);
      if (check.xp != null && check.xp.compareTo(BigDecimal.ZERO) > 0) {
        userService.updateLevelByExp(user.id);
        //        leagueService.updateXp(user, check.xp);
      }
      if (check.partnerId != null) {
        redisService.clearMissionUser("PARTNER", user.id);
      } else {
        if (check.type == Enums.MissionType.ON_TIME_IN_APP) {
          redisService.clearMissionUser("REWARD_ONE_TIME", user.id);
        } else {
          redisService.clearMissionUser("REWARD", user.id);
        }
      }
      if (check.rewardType != null) {
        switch (check.rewardType) {
        case CYBER_BOX:
          if (check.amount > 0) {
            for (int i = 0; i < check.amount; i++) {
              UserItem.create(
                new UserItem(user, redisService.findItemByCode(Enums.ItemSpecial.CYBER_BOX.name()), null));
            }
            return new MissionRewardResponse(check.amount, check.rewardName, check.rewardImage);
          }
          break;
        case OPEN_MESH:
          if (check.amount > 0) {
            if (Event.updateTotalUsdt(new BigDecimal(check.amount), Enums.EventId.OPEN_MESH.getId())) {
              UserItem.create(
                new UserItem(user, redisService.findItemByCode(Enums.ItemSpecial.OPEN_MESH.name()), null));
              return new MissionRewardResponse(check.amount, check.rewardName, check.rewardImage);
            } else {
              Mission.update("amount = 0 where id = ?1 and amount > 0", check.id);
            }
          }
          break;
        case TIMPI:
          int a = new Random().nextInt(10000);
          if (a < (redisService.getSystemConfigInt(Enums.Config.RANDOM_PERCENT_TIMPI))) {
            if (Event.updateTotalUsdt(new BigDecimal(1L), Enums.EventId.TIMPI.getId())) {
              UserItem.create(new UserItem(user, redisService.findItemByCode(Enums.ItemSpecial.NTMPI.name()), null));
              return new MissionRewardResponse(1L, "NTMPI", check.rewardImage);
            } else {
              Mission.update("rewardType = null where id = ?1", check.id);
            }
          }
        case FLASHBACK:
          int b = new Random().nextInt(1000);
          if (b < (redisService.getSystemConfigInt(Enums.Config.RANDOM_PERCENT_FLASHBACK))) {
            if (Event.updateTotalUsdt(new BigDecimal(1L), Enums.EventId.FLASHBACK.getId())) {
              UserItem.create(
                new UserItem(user, redisService.findItemByCode(Enums.ItemSpecial.FLASHBACK.name()), null));
              return new MissionRewardResponse(1L, "Flashback Ticket", check.rewardImage);
            } else {
              Mission.update("rewardType = null where id = ?1", check.id);
            }
          }
          break;
        case VENTURE_MIND_AI:
          int c = new Random().nextInt(1000);
          if (c < (redisService.getSystemConfigInt(Enums.Config.RANDOM_PERCENT_VENTURE_MIND_AI))) {
            if (Event.updateTotalUsdt(new BigDecimal(1L), Enums.EventId.VENTURE_MIND_AI.getId())) {
              UserItem.create(
                new UserItem(user, redisService.findItemByCode(Enums.ItemSpecial.USDT_5.name()), null));
              return new MissionRewardResponse(1L, "5 $USDT", check.rewardImage);
            } else {
              Mission.update("rewardType = null where id = ?1", check.id);
            }
          }
          break;
        }
      }
      return null;
    }
    throw new BusinessException(ResponseMessageConstants.MISSION_CLAIM_ERROR);
  }

  public List<GroupMissionResponse> getMissionReward(User user) {
    List<UserMissionResponse> userMissions = redisService.findMissionRewardNotOneTime(user.id, false);
    List<GroupMissionResponse> groupMissions = new ArrayList<>();
    for (UserMissionResponse userMission : userMissions) {
      GroupMissionResponse groupMission = groupMissions.stream()
        .filter(item -> item.group.equalsIgnoreCase(userMission.groupMission)).findFirst().orElse(null);
      if (groupMission == null) {
        groupMission = new GroupMissionResponse();
        groupMission.group = userMission.groupMission;
        groupMission.missions.add(userMission);
        groupMissions.add(groupMission);
      } else {
        groupMission.missions.add(userMission);
      }
    }
    long level = user.level.id;
    long countFriend = user.totalFriend;
    long countFriendEvent = user.totalFriend;
    List<UserMissionResponse> userMissionProduct = redisService.findMissionRewardOneTime(user.id);
    boolean isHasMissionLevel = false;
    boolean isHasMissionInvite = false;
    boolean isHasMissionInviteEvent = false;
    List<Long> rangeLevel = Arrays.asList(5L, 10L, 20L, 35L, 50L);
    List<Long> rangeInviteLevel = Arrays.asList(1L, 5L, 10L);
    List<Long> rangeInviteEvent = Arrays.asList(3L, 8L, 13L, 18L, 23L, 28L, 33L, 38L, 43L, 48L, 53L, 58L, 63L, 68L, 73L,
      78L, 83L, 88L, 93L, 98L);
    for (UserMissionResponse userMission : userMissionProduct) {
      if (userMission.missionRequire.name().startsWith("LEVEL_")) {
        if (isHasMissionLevel) {
          continue;
        }
        long numberRequire = Long.parseLong(userMission.missionRequire.name().replace("LEVEL_", ""));
        for (Long r : rangeLevel) {
          if (level < r && numberRequire > r) {
            break;
          }
        }
        isHasMissionLevel = true;
      } else if (userMission.missionRequire.name().startsWith("INVITE_")) {
        if (isHasMissionInvite) {
          continue;
        }
        long numberRequire = Long.parseLong(userMission.missionRequire.name().replace("INVITE_", ""));
        for (Long r : rangeInviteLevel) {
          if (countFriend < r && numberRequire > r) {
            break;
          }
        }
        isHasMissionInvite = true;
      } else if (userMission.missionRequire.name().startsWith("EVENT_INVITE_")) {
        if (isHasMissionInviteEvent) {
          continue;
        }
        long numberRequire = Long.parseLong(userMission.missionRequire.name().replace("EVENT_INVITE_", ""));
        for (Long r : rangeInviteEvent) {
          if (countFriendEvent < r && numberRequire > r) {
            break;
          }
        }
        isHasMissionInviteEvent = true;
      }
      GroupMissionResponse groupMission = groupMissions.stream()
        .filter(item -> item.group.equalsIgnoreCase(userMission.groupMission)).findFirst().orElse(null);
      if (groupMission == null) {
        groupMission = new GroupMissionResponse();
        groupMission.group = userMission.groupMission;
        groupMission.missions.add(userMission);
        groupMissions.add(groupMission);
      } else {
        groupMission.missions.add(userMission);
      }
    }
    return groupMissions;

  }

  @Transactional
  public String verifyMissionDaily(Long userId, long missionId, List<QuizResponse> answerArrays) {
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000;
    MissionDaily missionDaily = redisService.findMissionDailyById(missionId);
    if (missionDaily == null || missionDaily.date != currentDate) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    UserMissionDaily userMissionDaily = UserMissionDaily.find("user.id = ?1 and mission.id =?2", userId, missionId)
      .firstResult();
    if (userMissionDaily != null && userMissionDaily.status != null && userMissionDaily.status != Enums.MissionStatus.NOT_VERIFIED) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    boolean isChecked = false;
    if (missionDaily.isFake) {
      isChecked = true;
    } else {
      switch (missionDaily.type) {
      case LIKE_TWITTER:
      case FOLLOW_TWITTER:
      case RETWEETS:
      case TWEET_REPLIES:
        UserSocial userSocial = redisService.findUserSocial(userId);
        if (userSocial == null || StringUtils.isBlank(userSocial.twitterUsername)) {
          throw new BusinessException(ResponseMessageConstants.USER_MUST_LINK_TWITTER);
        }
        if (userMissionDaily != null && userMissionDaily.id > 0) {
          Map<String, Object> params = new HashMap<>();
          params.put("id", userMissionDaily.id);
          params.put("status", Enums.MissionStatus.VERIFYING);
          params.put("updatedAt", Utils.getCalendar().getTimeInMillis());
          UserMissionDaily.updateObject("status = :status, updatedAt = :updatedAt where id = :id and status is null",
            params);
        } else {
          userMissionDaily = new UserMissionDaily();
          userMissionDaily.mission = missionDaily;
          userMissionDaily.user = new User(userId);
          userMissionDaily.status = Enums.MissionStatus.VERIFYING;
          userMissionDaily.twitterUid = userSocial.twitterUid;
          UserMissionDaily.create(userMissionDaily);
        }
        redisService.clearMissionDaily(userId, currentDate);
        return "verifying";
      default:
        break;
      }
    }
    if (isChecked) {
      userMissionDaily = new UserMissionDaily();
      userMissionDaily.mission = missionDaily;
      userMissionDaily.user = new User(userId);
      userMissionDaily.status = Enums.MissionStatus.VERIFYING;
      UserMissionDaily.create(userMissionDaily);
      redisService.clearMissionDaily(userId, currentDate);
      return "true";
    }
    return "false";
  }

  @Transactional
  public MissionRewardResponse claimMissionDaily(long userId, long missionId) throws BusinessException {
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000;
    MissionDaily missionDaily = redisService.findMissionDailyById(missionId);
    if (missionDaily == null || missionDaily.date != currentDate) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    UserMissionDaily userMissionDaily = UserMissionDaily.find("user.id = ?1 and mission.id =?2", userId, missionId)
      .firstResult();
    if (userMissionDaily == null || userMissionDaily.status != Enums.MissionStatus.VERIFIED) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("missionId", missionId);
    params.put("userId", userId);
    params.put("statusNew", Enums.MissionStatus.CLAIMED);
    params.put("statusOld", Enums.MissionStatus.VERIFIED);
    if (UserMissionDaily.updateObject(
      "status = :statusNew where user.id = :userId and mission.id = :missionId and status = :statusOld", params)) {
      User.updatePointAndXpUser(userId, missionDaily.point, missionDaily.xp);
      if (missionDaily.xp != null && missionDaily.xp.compareTo(BigDecimal.ZERO) > 0) {
        userService.updateLevelByExp(userId);
      }
      redisService.clearMissionDaily(userId, currentDate);
      return null;
    }
    throw new BusinessException(ResponseMessageConstants.MISSION_CLAIM_ERROR);
  }
}

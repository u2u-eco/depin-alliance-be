package xyz.telegram.depinalliance.schedule;

import io.quarkus.panache.common.Sort;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import xyz.telegram.depinalliance.common.configs.TwitterConfig;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;
import xyz.telegram.depinalliance.services.RedisService;
import xyz.telegram.depinalliance.services.TwitterService;
import xyz.telegram.depinalliance.services.UserService;

import java.math.BigDecimal;
import java.util.*;

@ApplicationScoped
public class ScheduleMissionTwitter {
  @Inject
  Logger logger;
  @Inject
  TwitterService twitterService;
  @Inject
  TwitterConfig twitterConfig;
  @Inject
  RedisService redisService;
  @Inject
  UserService userService;

  @Scheduled(every = "${expr.every.twitter}", identity = "task-twitter")
  void schedule() {
    //verify follow
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    List<Mission> missions = redisService.findListMissionFollowTwitter();
    for (Mission mission : missions) {
      List<UserMission> userMissions = UserMission.find("mission.id = ?1 and status = ?2 and updatedAt <= ?3",
        Sort.ascending("updatedAt"), mission.id, Enums.MissionStatus.VERIFYING, timeValidate).page(0, 40).list();
      for (UserMission userMission : userMissions) {
        userMission = UserMission.findById(userMission.id);
        if (userMission.status != Enums.MissionStatus.VERIFYING) {
          break;
        }
        boolean isVerify = false;
        try {
          UserSocial userSocial = redisService.findUserSocial(userMission.user.id);
          switch (mission.type) {
          case FOLLOW_TWITTER:
            isVerify = twitterService.isUserFollowing(userSocial.twitterUid.toString(), mission.referId);
            logger.info("Verify follow " + isVerify + " user " + userSocial.userId + " " + mission.referId);
            break;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        updateUserMission(isVerify, userMission.id, mission.partner, userMission.user.id);
      }
    }
  }

  @Scheduled(every = "${expr.every.twitter-like}", identity = "task-twitter-like")
  void scheduleLikeDaily() {
    //verify Like
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000L;
    MissionDaily mission = redisService.findMissionDailyByType(Enums.MissionType.LIKE_TWITTER);
    if (mission != null) {
      List<UserMissionDaily> userMissions = UserMissionDaily.find("mission.id = ?1 and status = ?2 and updatedAt <= ?3",
        Sort.ascending("updatedAt"), mission.id, Enums.MissionStatus.VERIFYING, timeValidate).list();
      for (UserMissionDaily userMission : userMissions) {
        updateUserMissionDaily(true, userMission.id, userMission.user.id, currentDate);
      }
    }
  }

  @Scheduled(every = "${expr.every.twitter-post-reply}", identity = "task-twitter-post")
  void schedulePostDaily() {
    //verify Post Reply
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000L;
    MissionDaily missionPost = redisService.findMissionDailyByType(Enums.MissionType.RETWEETS);
    List<Long> missionId = new ArrayList<>();
    if (missionPost != null) {
      missionId.add(missionPost.id);
    }
    if (!missionId.isEmpty()) {
      List<UserMissionDaily> userMissions = UserMissionDaily.find(
        "mission.id in (?1) and status = ?2 and updatedAt <= ?3", Sort.ascending("updatedAt"), missionId,
        Enums.MissionStatus.VERIFYING, timeValidate).page(0, 40).list();
      for (UserMissionDaily userMission : userMissions) {
        userMission = UserMissionDaily.findById(userMission.id);
        if (userMission.status != Enums.MissionStatus.VERIFYING) {
          break;
        }
        boolean isVerify = false;
        if (Objects.equals(userMission.mission.id, missionPost.id)) {
          isVerify = twitterService.isUserRetweet(userMission.twitterUid.toString(), missionPost.referId,
            missionPost.timeStart);
          logger.info("Verify retweet " + isVerify + " user " + userMission.twitterUid + " " + missionPost.referId);
        }
        updateUserMissionDaily(isVerify, userMission.id, userMission.user.id, currentDate);
      }
    }
  }

  @Scheduled(every = "${expr.every.twitter-post-reply}", identity = "task-twitter-reply")
  void scheduleReplyDaily() {
    //verify Post Reply
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000L;
    MissionDaily missionReply = redisService.findMissionDailyByType(Enums.MissionType.TWEET_REPLIES);
    List<Long> missionId = new ArrayList<>();
    if (missionReply != null) {
      missionId.add(missionReply.id);
    }
    if (!missionId.isEmpty()) {
      List<UserMissionDaily> userMissions = UserMissionDaily.find(
        "mission.id in (?1) and status = ?2 and updatedAt <= ?3", Sort.ascending("updatedAt"), missionId,
        Enums.MissionStatus.VERIFYING, timeValidate).page(0, 40).list();
      for (UserMissionDaily userMission : userMissions) {
        userMission = UserMissionDaily.findById(userMission.id);
        if (userMission.status != Enums.MissionStatus.VERIFYING) {
          break;
        }
        boolean isVerify = false;
        if (Objects.equals(userMission.mission.id, missionReply.id)) {
          isVerify = twitterService.isUserReply(userMission.twitterUid.toString(), missionReply.referId,
            missionReply.timeStart);
          logger.info("Verify reply " + isVerify + " user " + userMission.twitterUid + " " + missionReply.referId);
        }
        updateUserMissionDaily(isVerify, userMission.id, userMission.user.id, currentDate);
      }
    }
  }

  @Scheduled(cron = "${expr.twitter-claim-old}", identity = "task-twitter-claim")
  void scheduleClaimOldMission() {
    //claim
    Calendar date = Utils.getNewDay();
    date.add(Calendar.DAY_OF_MONTH, -1);
    List<MissionDaily> missionDailies = MissionDaily.find("date <= ?1", date.getTimeInMillis() / 1000).list();
    missionDailies.forEach(missionDaily -> {
      List<UserMissionDaily> userMissionDailies = UserMissionDaily.list("mission.id = ?1 and status = ?2",
        missionDaily.id, Enums.MissionStatus.VERIFIED);
      userMissionDailies.forEach(userMissionDaily -> {
        try {
          claimUserMissionDaily(missionDaily, userMissionDaily.user.id);
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    });

  }

  @Transactional
  public void updateUserMission(boolean isVerify, long userMissionId, Partner partnerId, long userId) {
    if (!isVerify) {
      Map<String, Object> params = new HashMap<>();
      params.put("id", userMissionId);
      params.put("status", Enums.MissionStatus.VERIFYING);
      UserMission.updateObject("status = null where id = :id and status = :status", params);
    } else {
      Map<String, Object> params = new HashMap<>();
      params.put("id", userMissionId);
      params.put("statusNew", Enums.MissionStatus.VERIFIED);
      params.put("statusOld", Enums.MissionStatus.VERIFYING);
      UserMission.updateObject("status = :statusNew where id = :id and status = :statusOld", params);
    }

    if (partnerId != null) {
      redisService.clearMissionUser("PARTNER", userId);
    } else {
      redisService.clearMissionUser("REWARD", userId);
    }
  }

  @Transactional
  public void updateUserMissionDaily(boolean isVerify, long userMissionId, long userId, long currentDate) {
    if (!isVerify) {
      Map<String, Object> params = new HashMap<>();
      params.put("id", userMissionId);
      params.put("status", Enums.MissionStatus.VERIFYING);
      UserMissionDaily.updateObject("status = null where id = :id and status = :status", params);
    } else {
      Map<String, Object> params = new HashMap<>();
      params.put("id", userMissionId);
      params.put("statusNew", Enums.MissionStatus.VERIFIED);
      params.put("statusOld", Enums.MissionStatus.VERIFYING);
      UserMissionDaily.updateObject("status = :statusNew where id = :id and status = :statusOld", params);
    }
    redisService.clearMissionDaily(userId, currentDate);
  }

  @Transactional
  public void claimUserMissionDaily(MissionDaily missionDaily, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("missionId", missionDaily.id);
    params.put("userId", userId);
    params.put("statusNew", Enums.MissionStatus.CLAIMED);
    params.put("statusOld", Enums.MissionStatus.VERIFIED);
    if (UserMissionDaily.updateObject(
      "status = :statusNew where user.id = :userId and mission.id = :missionId and status = :statusOld", params)) {
      User.updatePointAndXpUser(userId, missionDaily.point, missionDaily.xp);
      if (missionDaily.xp != null && missionDaily.xp.compareTo(BigDecimal.ZERO) > 0) {
        userService.updateLevelByExp(userId);
      }
//      redisService.clearMissionDaily(userId, missionDaily.date);
    }
  }
}

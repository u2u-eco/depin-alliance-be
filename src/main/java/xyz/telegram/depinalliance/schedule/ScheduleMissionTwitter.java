package xyz.telegram.depinalliance.schedule;

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

  @Scheduled(every = "${expr.every.twitter}", identity = "task-twitter")
  void schedule() {
    //verify follow
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    List<Mission> missions = redisService.findListMissionFollowTwitter();
    for (Mission mission : missions) {
      List<UserMission> userMissions = UserMission.find("mission.id = ?1 and status = ?2 and updatedAt <= ?3",
        mission.id, Enums.MissionStatus.VERIFYING, timeValidate).list();
      for (UserMission userMission : userMissions) {
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
        mission.id, Enums.MissionStatus.VERIFYING, timeValidate).list();
      for (UserMissionDaily userMission : userMissions) {
        updateUserMissionDaily(true, userMission.id, userMission.user.id, currentDate);
      }
    }
  }

  @Scheduled(every = "${expr.every.twitter-post-reply}", identity = "task-twitter-post-reply")
  void schedulePostReplyDaily() {
    //verify Post Reply
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000L;
    MissionDaily missionReply = redisService.findMissionDailyByType(Enums.MissionType.TWEET_REPLIES);
    MissionDaily missionPost = redisService.findMissionDailyByType(Enums.MissionType.RETWEETS);
    List<Long> missionId = new ArrayList<>();
    if (missionReply != null) {
      missionId.add(missionReply.id);
    }
    if (missionPost != null) {
      missionId.add(missionPost.id);
    }
    if (!missionId.isEmpty()) {
      List<UserMissionDaily> userMissions = UserMissionDaily.find(
        "mission.id in (?1) and status = ?2 and updatedAt <= ?3", missionId, Enums.MissionStatus.VERIFYING,
        timeValidate).list();

      for (UserMissionDaily userMission : userMissions) {
        boolean isVerify = false;
        if (missionReply != null && Objects.equals(userMission.mission.id, missionReply.id)) {
          isVerify = twitterService.isUserReply(userMission.twitterUid.toString(), missionReply.referId,
            missionReply.timeStart);
          logger.info("Verify reply " + isVerify + " user " + userMission.twitterUid + " " + missionReply.referId);
        } else if (missionPost != null && Objects.equals(userMission.mission.id, missionPost.id)) {
          isVerify = twitterService.isUserRetweet(userMission.twitterUid.toString(), missionPost.referId,
            missionPost.timeStart);
          logger.info("Verify retweet " + isVerify + " user " + userMission.twitterUid + " " + missionPost.referId);
        }
        updateUserMissionDaily(isVerify, userMission.id, userMission.user.id, currentDate);
      }
    }
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
}

package xyz.telegram.depinalliance.schedule;

import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
  @ConfigProperty(name = "expr.every.twitter")
  String scheduleTwitterFollow;
  @ConfigProperty(name = "expr.every.twitter-like")
  String scheduleTwitterLike;
  @ConfigProperty(name = "expr.every.twitter-post")
  String scheduleTwitterPost;
  @ConfigProperty(name = "expr.every.twitter-reply")
  String scheduleTwitterReply;
  @ConfigProperty(name = "expr.every.twitter-quote")
  String scheduleTwitterQuote;

  @ActivateRequestContext
  void onStart(@Observes StartupEvent event) {
    if (StringUtils.isNotBlank(scheduleTwitterFollow) && !"disabled".equals(scheduleTwitterFollow)) {
      new Thread(() -> {
        while (true) {
          try {
            scheduleFollow();
          } catch (Exception exception) {
            exception.printStackTrace();
          } finally {
            try {
              Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
          }
        }
      }).start();
    }
    if (StringUtils.isNotBlank(scheduleTwitterLike) && !"disabled".equals(scheduleTwitterLike)) {
      new Thread(() -> {
        while (true) {
          try {
            scheduleLikeDaily();
          } catch (Exception ignored) {
          } finally {
            try {
              Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
          }
        }
      }).start();
    }

    if (StringUtils.isNotBlank(scheduleTwitterPost) && !"disabled".equals(scheduleTwitterPost)) {
      new Thread(() -> {
        while (true) {
          try {
            schedulePostDaily();
          } catch (Exception ignored) {
          } finally {
            try {
              Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
          }
        }
      }).start();
    }
    if (StringUtils.isNotBlank(scheduleTwitterReply) && !"disabled".equals(scheduleTwitterReply)) {
      new Thread(() -> {
        while (true) {
          try {
            scheduleReplyDaily();
          } catch (Exception ignored) {
          } finally {
            try {
              Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
          }
        }
      }).start();

    }
    if (StringUtils.isNotBlank(scheduleTwitterQuote) && !"disabled".equals(scheduleTwitterQuote)) {
      new Thread(() -> {
        while (true) {
          try {
            scheduleQuoteDaily();
          } catch (Exception ignored) {
          } finally {
            try {
              Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
          }
        }
      }).start();
    }
  }

  //  @Scheduled(every = "${expr.every.twitter}", identity = "task-twitter")
  @ActivateRequestContext
  void scheduleFollow() {
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
          if (userSocial != null && StringUtils.isNotBlank(userSocial.twitterUsername)) {
            isVerify = twitterService.isUserFollowing(userSocial.twitterUid.toString(), mission.referId);
            logger.info("Verify follow " + isVerify + " user " + userMission.user.id + " " + mission.referId);
          } else {

          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        updateUserMission(isVerify, userMission.id, mission.partner, userMission.user.id);
      }
    }
  }

  //  @Scheduled(every = "${expr.every.twitter-like}", identity = "task-twitter-like")
  @ActivateRequestContext
  void scheduleLikeDaily() {
    //verify Like
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000L;
    List<MissionDaily> missions = redisService.findMissionDailyByType(Enums.MissionType.LIKE_TWITTER);
    if (missions != null) {
      List<Long> missionId = new ArrayList<>();
      missions.forEach(mission -> missionId.add(mission.id));

      List<UserMissionDaily> userMissions = UserMissionDaily.find(
        "mission.id in (?1) and status = ?2 and updatedAt <= ?3", Sort.ascending("updatedAt"), missionId,
        Enums.MissionStatus.VERIFYING, timeValidate).list();
      for (UserMissionDaily userMission : userMissions) {
        UserSocial userSocial = redisService.findUserSocial(userMission.user.id);
        if (userSocial != null && StringUtils.isNotBlank(userSocial.twitterUsername)) {
          updateUserMissionDaily(true, userMission.id, userMission.user.id, currentDate);
        } else {
          logger.info("Verify like false user " + userMission.user.id + " not link twitter");
        }
      }
    }
  }

  //  @Scheduled(every = "${expr.every.twitter-post-reply}", identity = "task-twitter-post")
  @ActivateRequestContext
  void schedulePostDaily() {
    //verify Post Reply
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000L;
    List<MissionDaily> missionPosts = redisService.findMissionDailyByType(Enums.MissionType.RETWEETS);
    List<Long> missionId = new ArrayList<>();
    Map<Long, MissionDaily> missionDailyMap = new HashMap<>();
    if (missionPosts != null) {
      missionPosts.forEach(mission -> {
        missionDailyMap.put(mission.id, mission);
        missionId.add(mission.id);
      });
    }
    if (!missionId.isEmpty()) {
      List<UserMissionDaily> userMissions = UserMissionDaily.find(
        "mission.id in (?1) and status = ?2 and updatedAt <= ?3", Sort.ascending("updatedAt"), missionId,
        Enums.MissionStatus.VERIFYING, timeValidate).page(0, 40).list();
      for (UserMissionDaily userMission : userMissions) {
        boolean isVerify = false;
        MissionDaily missionPost = missionDailyMap.get(userMission.mission.id);
        UserSocial userSocial = redisService.findUserSocial(userMission.user.id);
        if (userSocial != null && StringUtils.isNotBlank(userSocial.twitterUsername)) {
          if (missionPost.isFake) {
            isVerify = true;
            logger.info("Fake retweet " + isVerify + " user " + userSocial.twitterUid + " " + missionPost.referId);
          } else {
            isVerify = twitterService.isUserRetweet(userSocial.twitterUid.toString(), missionPost.referId,
              missionPost.timeStart);
            logger.info("Verify retweet " + isVerify + " user " + userSocial.twitterUid + " " + missionPost.referId);
          }
        } else {
          logger.info("Verify retweet " + isVerify + " user " + userMission.user.id + " not link twitter");
        }
        updateUserMissionDaily(isVerify, userMission.id, userMission.user.id, currentDate);
      }
    }
  }

  //  @Scheduled(every = "${expr.every.twitter-post-reply}", identity = "task-twitter-reply")
  @ActivateRequestContext
  void scheduleReplyDaily() {
    //verify Post Reply
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000L;
    List<MissionDaily> missionReplies = redisService.findMissionDailyByType(Enums.MissionType.TWEET_REPLIES);
    List<Long> missionId = new ArrayList<>();
    Map<Long, MissionDaily> missionDailyMap = new HashMap<>();
    if (missionReplies != null) {
      missionReplies.forEach(mission -> {
        missionDailyMap.put(mission.id, mission);
        missionId.add(mission.id);
      });
    }
    if (!missionId.isEmpty()) {
      List<UserMissionDaily> userMissions = UserMissionDaily.find(
        "mission.id in (?1) and status = ?2 and updatedAt <= ?3", Sort.ascending("updatedAt"), missionId,
        Enums.MissionStatus.VERIFYING, timeValidate).page(0, 40).list();
      for (UserMissionDaily userMission : userMissions) {
        boolean isVerify = false;
        MissionDaily missionReply = missionDailyMap.get(userMission.mission.id);
        UserSocial userSocial = redisService.findUserSocial(userMission.user.id);
        if (userSocial != null && StringUtils.isNotBlank(userSocial.twitterUsername)) {
          if (missionReply.isFake) {
            isVerify = true;
            logger.info("Fake reply " + isVerify + " user " + userSocial.twitterUid + " " + missionReply.referId);
          } else {
            isVerify = twitterService.isUserReply(userSocial.twitterUid.toString(), missionReply.referId,
              missionReply.timeStart);
            logger.info("Verify reply " + isVerify + " user " + userSocial.twitterUid + " " + missionReply.referId);
          }
        } else {
          logger.info("Verify reply " + isVerify + " user " + userMission.user.id + " not link twitter");
        }
        updateUserMissionDaily(isVerify, userMission.id, userMission.user.id, currentDate);
      }
    }
  }

  @ActivateRequestContext
  void scheduleQuoteDaily() {
    //verify Quote
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    long currentDate = Utils.getNewDay().getTimeInMillis() / 1000L;
    List<MissionDaily> missionQuotes = redisService.findMissionDailyByType(Enums.MissionType.TWEET_QUOTE);
    List<Long> missionId = new ArrayList<>();
    Map<Long, MissionDaily> missionDailyMap = new HashMap<>();
    if (missionQuotes != null) {
      missionQuotes.forEach(mission -> {
        missionDailyMap.put(mission.id, mission);
        missionId.add(mission.id);
      });
    }
    if (!missionId.isEmpty()) {
      List<UserMissionDaily> userMissions = UserMissionDaily.find(
        "mission.id in (?1) and status = ?2 and updatedAt <= ?3", Sort.ascending("updatedAt"), missionId,
        Enums.MissionStatus.VERIFYING, timeValidate).page(0, 40).list();
      for (UserMissionDaily userMission : userMissions) {
        boolean isVerify = false;
        UserSocial userSocial = redisService.findUserSocial(userMission.user.id);
        MissionDaily missionQuote = missionDailyMap.get(userMission.mission.id);
        if (userSocial != null && StringUtils.isNotBlank(userSocial.twitterUsername)) {
          if (missionQuote.isFake) {
            isVerify = true;
            logger.info("Fake quote " + isVerify + " user " + userSocial.twitterUid + " " + missionQuote.referId);
          } else {
            isVerify = twitterService.isUserQuote(userSocial.twitterUid.toString(), missionQuote.referId,
              missionQuote.timeStart);
            logger.info("Verify quote " + isVerify + " user " + userSocial.twitterUid + " " + missionQuote.referId);
          }
        } else {
          logger.info("Verify quote " + isVerify + " user " + userMission.user.id + " not link twitter");
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

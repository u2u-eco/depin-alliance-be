package xyz.telegram.depinalliance.schedule;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import xyz.telegram.depinalliance.common.configs.TwitterConfig;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.Mission;
import xyz.telegram.depinalliance.entities.Partner;
import xyz.telegram.depinalliance.entities.UserMission;
import xyz.telegram.depinalliance.entities.UserSocial;
import xyz.telegram.depinalliance.services.RedisService;
import xyz.telegram.depinalliance.services.TwitterService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    //      @Scheduled(cron = "00 19 15 * * ?", identity = "task-twitter")
  void schedule() {
    //verify follow
    long currentTime = Utils.getCalendar().getTimeInMillis();
    long timeValidate = currentTime - twitterConfig.verifyTime();
    List<Mission> missions = redisService.findListMissionFollowTwitter();
    for (Mission mission : missions) {
      List<UserMission> userMissions = UserMission.find("mission.id = ?1 and status = ?2 and updatedAt <= ?3",
        mission.id, Enums.MissionStatus.VERIFYING, timeValidate).list();
      System.out.println(userMissions.size());
      for (UserMission userMission : userMissions) {
        boolean isVerify = false;
        try {
          UserSocial userSocial = redisService.findUserSocial(userMission.user.id);
          switch (mission.type) {
          case FOLLOW_TWITTER:
            isVerify = twitterService.isUserFollowing(userSocial.twitterUid.toString(), mission.referId);
            break;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        updateUserMission(isVerify, userMission.id, mission.partner, userMission.user.id);
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
}

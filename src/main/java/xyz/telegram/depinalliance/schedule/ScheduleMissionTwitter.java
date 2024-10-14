package xyz.telegram.depinalliance.schedule;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import xyz.telegram.depinalliance.common.models.response.TwitterFollowResponse;
import xyz.telegram.depinalliance.services.TwitterClient;

@ApplicationScoped
public class ScheduleMissionTwitter {
  @Inject
  Logger logger;
  @RestClient
  TwitterClient twitterClient;

  @Scheduled(every = "${expr.every.twitter}", identity = "task-twitter")
  void schedule() {
    try {
      String continuationToken = "";
      while (true) {
        TwitterFollowResponse res = StringUtils.isBlank(continuationToken) ?
          twitterClient.getFollower("1577973357773225984") :
          twitterClient.getFollowerContinuation("1577973357773225984", continuationToken);
        if (StringUtils.isNotBlank(res.continuationToken)) {
          continuationToken = res.continuationToken;
        }
        if (res.results == null || res.results.isEmpty()) {
          break;
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    //    List<Mission> missions = Mission.findByMissionTwitter();
    //    for (Mission mission : missions) {
    //      List<UserMission> userMissions = UserMission.list("mission.id = ?1 and status = ?2", mission.id,
    //        Enums.MissionStatus.VERIFYING);
    //
    //      //1577973357773225984
    //    }
  }
}

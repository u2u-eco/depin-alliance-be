package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.PartnerResponse;
import xyz.telegram.depinalliance.common.models.response.QuizResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.models.response.UserMissionResponse;
import xyz.telegram.depinalliance.entities.MissionDaily;
import xyz.telegram.depinalliance.entities.UserMissionDaily;
import xyz.telegram.depinalliance.services.MissionService;
import xyz.telegram.depinalliance.services.RedisService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author holden on 26-Aug-2024
 */
@Path("missions")
public class MissionResource extends BaseResource {

  @Inject
  MissionService missionService;
  @Inject
  RedisService redisService;

  @GET
  @Path("daily-checkin")
  public ResponseData getDailyCheckin() throws BusinessException {
    return ResponseData.ok(missionService.getListOfDailyCheckin(getUser()));
  }

  @POST
  @Path("daily-checkin")
  public ResponseData checkin() throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(missionService.checkin(getUser()));
    }
  }

  @GET
  @Path("")
  public ResponseData getAllMissions() {
    return ResponseData.ok(missionService.getMissionReward(getUser()));
  }

  @GET
  @Path("partner")
  public ResponseData getMissionPartner() {
    List<PartnerResponse> partners = redisService.findPartner();
    List<UserMissionResponse> userMissionsPartner = redisService.findMissionRewardNotOneTime(getTelegramId(), true);
    partners.forEach(partner -> partner.missions = userMissionsPartner.stream()
      .filter(mission -> mission.groupMission.equalsIgnoreCase(partner.name)).collect(Collectors.toList()));
    return ResponseData.ok(partners);
  }

  @GET
  @Path("daily")
  public ResponseData<?> getMissionDaily() {
    List<MissionDaily> missionDaily = redisService.findMissionDaily();
    List<Long> missionIds = missionDaily.stream().map(mission -> mission.id).toList();
    List<UserMissionDaily> userMissionDailies = UserMissionDaily.list("mission.id in (?1)", missionIds);
    List<UserMissionResponse> userMissions = new ArrayList<>();
    missionDaily.forEach(mission -> {
      UserMissionDaily userMissionDaily = userMissionDailies.stream()
        .filter(userMission -> userMission.mission.id.equals(mission.id)).findFirst().orElse(null);
      UserMissionResponse userMissionResponse = new UserMissionResponse(mission.id, "Mission Daily", mission.name,
        mission.image, mission.description, mission.type, mission.url, mission.point, mission.xp,
        userMissionDaily != null ? userMissionDaily.status : null, mission.isFake, null, mission.amount,
        mission.referId, null, mission.rewardType, mission.rewardImage);
      userMissions.add(userMissionResponse);
    });
    return ResponseData.ok(userMissions);
  }

  @GET
  @Path("verify-task/{id}")
  public ResponseData verifyTask(@PathParam("id") Long missionId) {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(missionService.verify(getUser(), missionId, null));
    }
  }

  @POST
  @Path("verify-task/{id}")
  public ResponseData verifyTaskQuiz(@PathParam("id") Long missionId, List<QuizResponse> answerRequests) {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(missionService.verify(getUser(), missionId, answerRequests));
    }
  }

  @GET
  @Path("claim-task/{id}")
  public ResponseData claimTask(@PathParam("id") Long missionId) throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(missionService.claim(getUser(), missionId));
    }
  }
}

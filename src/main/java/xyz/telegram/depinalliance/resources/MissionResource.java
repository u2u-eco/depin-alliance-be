package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.GroupMissionResponse;
import xyz.telegram.depinalliance.common.models.response.PartnerResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.models.response.UserMissionResponse;
import xyz.telegram.depinalliance.entities.Mission;
import xyz.telegram.depinalliance.entities.Partner;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.services.MissionService;

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
    List<UserMissionResponse> userMissions = Mission.findByUserId(getTelegramId(), false);
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
    User user = getUser();
    long level = user.level.id;
    List<UserMissionResponse> userMissionProduct = Mission.findTypeOnTimeInAppByUserId(getTelegramId());
    for (UserMissionResponse userMission : userMissionProduct) {
      if (userMission.missionRequire.name().contains("LEVEL_")) {
        long levelRequire = Long.valueOf(userMission.missionRequire.name().replace("LEVEL_", ""));
        if (level < 5 && levelRequire > 5) {
          break;
        }
        if (level < 10 && levelRequire > 10) {
          break;
        }
        if (level < 20 && levelRequire > 20) {
          break;
        }
        if (level < 35 && levelRequire > 35) {
          break;
        }
        if (level < 50 && levelRequire > 50) {
          break;
        }
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
    return ResponseData.ok(groupMissions);
  }

  @GET
  @Path("partner")
  public ResponseData getMissionPartner() {
    List<PartnerResponse> partners = Partner.findAllPartner();
    List<UserMissionResponse> userMissionsPartner = Mission.findByUserId(getTelegramId(), true);
    partners.forEach(partner -> partner.missions = userMissionsPartner.stream()
      .filter(mission -> mission.groupMission.equalsIgnoreCase(partner.name)).collect(Collectors.toList()));
    return ResponseData.ok(partners);
  }

  @GET
  @Path("verify-task/{id}")
  public ResponseData verifyTask(@PathParam("id") Long missionId) throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(missionService.verify(getUser(), missionId));
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

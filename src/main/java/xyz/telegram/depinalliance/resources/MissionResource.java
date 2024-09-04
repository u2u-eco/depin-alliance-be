package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.GroupMissionResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.models.response.UserMissionResponse;
import xyz.telegram.depinalliance.entities.Mission;
import xyz.telegram.depinalliance.services.MissionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    List<UserMissionResponse> userMissions = Mission.findByUserId(getTelegramId());
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
    return ResponseData.ok(groupMissions);
  }

  @GET
  @Path("v2")
  public ResponseData getAllMissionsV2() {
    List<UserMissionResponse> userMissions = Mission.findByUserId(getTelegramId());




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
    return ResponseData.ok(groupMissions);
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

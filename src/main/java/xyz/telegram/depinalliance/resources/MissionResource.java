package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.services.MissionService;

/**
 * @author holden on 26-Aug-2024
 */
@Path("missions")
public class MissionResource extends BaseResource {

  @Inject
  MissionService missionService;

  @GET
  @Path("daily-checkin")
  public ResponseData getDailyCheckin() {
    return ResponseData.ok(missionService.getListOfDailyCheckin(getUser()));
  }

  @POST
  @Path("daily-checkin")
  public ResponseData checkin() throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(missionService.checkin(getUser()));
    }
  }
}

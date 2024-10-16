package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.models.request.MissionDailyRequest;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.services.WorldMapService;
import xyz.telegram.depinalliance.services.RedisService;

/**
 * @author holden on 04-Oct-2024
 */
@Path("/mission-daily")
public class WorldMapResource extends BaseResource {

  @Inject
  RedisService redisService;
  @Inject
  WorldMapService worldMapService;

  /*@GET
  @Path("")
  public ResponseData<?> missionDaily() {
    return ResponseData.ok(redisService.findMissionDaily(getTelegramId()));
  }

  @POST
  @Path("new-mission-daily")
  public ResponseData<?> newMissionDaily(MissionDailyRequest request) {
    worldMapService.newMissionDaily(getTelegramId(), request);
    clearMissionDaily(getTelegramId());
    return ResponseData.ok(redisService.findMissionDaily(getTelegramId()));
  }

  @GET
  @Path("mission-daily-start/{number}")
  public ResponseData<?> missionDailyStart(@PathParam("number") int number) {
    Object res = worldMapService.missionDailyStart(getTelegramId(), number);
    clearMissionDaily(getTelegramId());
    return ResponseData.ok(res);
  }

  @GET
  @Path("mission-item/{type}")
  public ResponseData<?> missionItemList(@PathParam("type") String type) {
    if (StringUtils.isBlank(type)) {
      return ResponseData.error(ResponseMessageConstants.DATA_INVALID);
    }
    Enums.MissionItemType missionItemType = Enums.MissionItemType.valueOf(type.toUpperCase());
    return ResponseData.ok(redisService.findListGameItemByType(missionItemType));
  }*/

  public void clearMissionDaily(long userId) {
    redisService.clearCacheByPrefix("MISSION_DAILY_" + userId);
  }
}

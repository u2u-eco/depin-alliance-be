package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.models.request.WorldMapRequest;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.services.RedisService;
import xyz.telegram.depinalliance.services.WorldMapService;

/**
 * @author holden on 04-Oct-2024
 */
@Path("/world-map")
public class WorldMapResource extends BaseResource {

  @Inject
  RedisService redisService;
  @Inject
  WorldMapService worldMapService;

  @GET
  @Path("")
  public ResponseData<?> worldMap() {
    return ResponseData.ok(redisService.findWorldMap(getTelegramId()));
  }

  @POST
  @Path("")
  public ResponseData<?> newWorldMap(WorldMapRequest request) {
    worldMapService.newWorldMap(getTelegramId(), request);
    redisService.clearWorldMap(getTelegramId());
    return ResponseData.ok(redisService.findWorldMap(getTelegramId()));
  }

  @PUT
  @Path("")
  public ResponseData<?> changeItem(WorldMapRequest request) {
    worldMapService.changeItem(getTelegramId(), request);
    return ResponseData.ok(redisService.findWorldMap(getTelegramId()));
  }

  @GET
  @Path("start/{number}")
  public ResponseData<?> worldMapStart(@PathParam("number") int number) {
    Object res = worldMapService.worldMapStart(getTelegramId(), number);
    redisService.clearWorldMap(getTelegramId());
    return ResponseData.ok(res);
  }

  @POST
  @Path("end/{number}")
  public ResponseData<?> worldMapEnd(@PathParam("number") int number, Object object) {
    Object res = worldMapService.worldMapComplete(getTelegramId(), number, object);
    redisService.clearWorldMap(getTelegramId());
    return ResponseData.ok(res);
  }

  @GET
  @Path("item/{type}")
  public ResponseData<?> worldMapItem(@PathParam("type") String type) {
    if (StringUtils.isBlank(type)) {
      return ResponseData.error(ResponseMessageConstants.DATA_INVALID);
    }
    Enums.WorldMapItemType worldMapItemType = Enums.WorldMapItemType.valueOf(type.toUpperCase());
    return ResponseData.ok(redisService.findWorldMapItemByType(worldMapItemType));
  }

}

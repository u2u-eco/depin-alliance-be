package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.models.request.GameDailyRequest;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.GameDaily;
import xyz.telegram.depinalliance.entities.GameItem;
import xyz.telegram.depinalliance.entities.GameResult;
import xyz.telegram.depinalliance.services.RedisService;

import java.math.BigDecimal;

/**
 * @author holden on 04-Oct-2024
 */
@Path("/game")
public class GameResource extends BaseResource {

  @Inject
  RedisService redisService;

  @GET
  @Path("game-daily")
  public ResponseData<?> gameDaily() {
    return ResponseData.ok(redisService.findGameDaily(getTelegramId()));
  }

  @POST
  @Path("new-game-daily")
  @Transactional
  public ResponseData<?> newGameDaily(GameDailyRequest request) {
    if (request == null || StringUtils.isBlank(request.agency) || StringUtils.isBlank(
      request.continent) || StringUtils.isBlank(request.tool)) {
      return ResponseData.error(ResponseMessageConstants.DATA_INVALID);
    }

    GameDaily gameDaily = new GameDaily();
    gameDaily.userId = getTelegramId();
    gameDaily.date = Utils.getNewDay().getTimeInMillis() / 1000;
    gameDaily.time = 1;
    gameDaily.create();
    gameDaily.agency = new GameItem((long) Utils.getRandomNumber(1, 6));
    gameDaily.tool = new GameItem((long) Utils.getRandomNumber(6, 11));
    gameDaily.continent = new GameItem((long) Utils.getRandomNumber(11, 17));
    gameDaily.reward = new BigDecimal(5000000);

    gameDaily.persist();
    for (int i = 0; i < 5; i++) {
      int random = Utils.getRandomNumber(0, 1000);
      GameResult gameResult = new GameResult();
      gameResult.gameId = random;
      gameResult.userId = gameDaily.userId;
      gameResult.gameDaily = gameDaily;
      gameResult.type = Enums.GameType.SUDOKU;
      gameResult.persist();
    }
    return ResponseData.ok(gameDaily.id);
  }

  @GET
  @Path("game-item/{type}")
  public ResponseData<?> gameItemList(@PathParam("type") String type) {
    if (StringUtils.isBlank(type)) {
      return ResponseData.error(ResponseMessageConstants.DATA_INVALID);
    }
    Enums.GameItemType gameItemType = Enums.GameItemType.valueOf(type.toUpperCase());
    return ResponseData.ok(redisService.findListGameItemByType(gameItemType));
  }
}

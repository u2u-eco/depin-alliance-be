package xyz.telegram.depinalliance.resources;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.game.entities.GameDaily;
import xyz.telegram.depinalliance.game.entities.GameItem;
import xyz.telegram.depinalliance.game.entities.GameResult;

import java.math.BigDecimal;

/**
 * @author holden on 04-Oct-2024
 */
@Path("/game")
public class GameResource extends BaseResource {

  @GET
  @Path("new-game-sudoku")
  @Transactional
  public ResponseData newGame() {
    GameDaily gameDaily = new GameDaily();
    gameDaily.userId = getTelegramId();
    gameDaily.date = Utils.getNewDay().getTimeInMillis() / 1000;
    gameDaily.time = 1;
    gameDaily.create();
    gameDaily.agency = new GameItem(1L);
    gameDaily.tool = new GameItem(7L);
    gameDaily.continent = new GameItem(11L);
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

    return ResponseData.ok(gameDaily);
  }
}

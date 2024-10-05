package xyz.telegram.depinalliance.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import xyz.telegram.depinalliance.common.models.response.GameSudokuResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.game.entities.GameDaily;
import xyz.telegram.depinalliance.game.entities.GameSudoku;

/**
 * @author holden on 04-Oct-2024
 */
@Path("/game")
public class GameResource extends BaseResource {

  @GET
  @Path("new-game-sudoku")
  public ResponseData newGame() {
    GameDaily gameDaily = new GameDaily();
    gameDaily.userId = getTelegramId();
    gameDaily.date = Utils.getNewDay().getTimeInMillis()/1000;
    gameDaily.time = 1;
    gameDaily.create();
    gameDaily.

    int random = Utils.getRandomNumber(0, 1000);
    return ResponseData.ok(GameSudoku.find("id", random).project(GameSudokuResponse.class).firstResult());
  }
}

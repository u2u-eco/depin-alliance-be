package xyz.telegram.depinalliance.services;

import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.MissionDailyRequest;
import xyz.telegram.depinalliance.common.models.response.MissionDailyResponse;
import xyz.telegram.depinalliance.common.models.response.MissionResultResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Objects;

/**
 * @author holden on 09-Oct-2024
 */
@ApplicationScoped
public class WorldMapService {

  @Inject
  RedisService redisService;

 /* @Transactional
  public void newMissionDaily(long userId, MissionDailyRequest request) {
    if (request == null || StringUtils.isBlank(request.agency) || StringUtils.isBlank(
      request.continent) || StringUtils.isBlank(request.tool)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    MissionDailyResponse response = redisService.findMissionDaily(userId);
    if (response != null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemAgency = redisService.findMissionItemByCode(request.agency);
    if (itemAgency == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemContinent = redisService.findMissionItemByCode(request.continent);
    if (itemContinent == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemTool = redisService.findMissionItemByCode(request.tool);
    if (itemTool == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    WorldMap worldMap = new WorldMap();
    worldMap.user = new User(userId);
    worldMap.date = Utils.getNewDay().getTimeInMillis() / 1000;
    worldMap.time = 1;
    worldMap.create();
    worldMap.agency = itemAgency;
    worldMap.tool = itemTool;
    worldMap.continent = itemContinent;

    //mission 1
    worldMap.mission1Type = Enums.MissionDailyType.SUDOKU;
    worldMap.mission1Title = "Play sudoku";

    //mission 2
    worldMap.mission2Type = Enums.MissionDailyType.SUDOKU;
    worldMap.mission2Title = "Play sudoku";
    //mission 3
    worldMap.mission3Type = Enums.MissionDailyType.SUDOKU;
    worldMap.mission3Title = "Play sudoku";
    //mission 4
    worldMap.mission4Type = Enums.MissionDailyType.SUDOKU;
    worldMap.mission4Title = "Play sudoku";
    //mission 5
    worldMap.mission5Type = Enums.MissionDailyType.SUDOKU;
    worldMap.mission5Title = "Play sudoku";
    worldMap.persist();
  }

  @Transactional
  public Object missionDailyStart(long userId, int number) {
    MissionDailyResponse response = redisService.findMissionDaily(userId);
    if (response == null || number > 5 || number <= 0 || response.isCompleted) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    MissionResultResponse resultResponse = response.results.stream()
      .filter(result -> result.id == number && !result.isCompleted).findFirst().orElse(null);
    if (resultResponse == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Parameters parameters = new Parameters();
    String sql = "mission" + number + "Detail = :missionDetail , mission" + number + "CreatedAt = :createdAt where id = :id and user.id = :userId and mission" + number + "IsCompleted = false and isCompleted =false";
    Object res = null;
    if (resultResponse.type == Enums.MissionDailyType.SUDOKU) {
      res = redisService.findMissionSudokuById(Utils.getRandomNumber(0, redisService.findMaxMissionSudoku()));
      parameters.and("missionDetail", Utils.convertObjectToString(res));
    }
    parameters.and("id", response.id);
    parameters.and("userId", userId);
    parameters.and("createdAt", Utils.getCalendar().getTimeInMillis() / 1000);
    if (WorldMap.update(sql, parameters) == 1) {
      return res;
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }

  @Transactional
  public boolean missionDailyComplete(long userId, int number, Object obj) {
    MissionDailyResponse response = redisService.findMissionDaily(userId);
    if (response == null || number > 5 || number <= 0 || response.isCompleted || obj == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    MissionResultResponse resultResponse = response.results.stream()
      .filter(result -> result.id == number && !result.isCompleted).findFirst().orElse(null);
    if (resultResponse == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (resultResponse.type == Enums.MissionDailyType.SUDOKU) {
      if (StringUtils.isBlank(resultResponse.gameDetail)) {
        throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
      }
      MissionSudoku userResult = (MissionSudoku) obj;
      MissionSudoku missionSudoku = Utils.toObject(resultResponse.gameDetail, MissionSudoku.class);
      if (userResult.id.compareTo(0L) <= 0 || StringUtils.isBlank(userResult.mission) || !Objects.equals(userResult.id,
        Objects.requireNonNull(missionSudoku).id)) {
        throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
      }
      if (!userResult.solution.equals(missionSudoku.solution)) {
        throw new BusinessException(ResponseMessageConstants.MISSION_FAILED);
      }
    } else {
      return false;
    }
    long currentTime = Utils.getCalendar().getTimeInMillis() / 1000;
    Parameters parameters = new Parameters();
    String sql = "mission" + number + "EndedAt = :updatedAt, mission" + number + "IsCompleted = true, numberMissionCompleted = numberMissionCompleted + 1 ";
    parameters.and("id", response.id);
    parameters.and("userId", userId);
    parameters.and("updatedAt", currentTime);
    BigDecimal totalPointWin = response.currentRewardPoint;
    if (response.numberMissionCompleted == 4) {
      sql += ", isCompleted = true , nextTimePlay = :nextTimePlay ";
      long nextTime = currentTime + 1000;
      Calendar calendar = Utils.getNewDay();
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      if (nextTime > calendar.getTimeInMillis() / 1000) {
        nextTime = 0;
      }
      parameters.and("nextTimePlay", nextTime);
      WorldMapDailyCombo worldMapDailyCombo = redisService.findMissionDailyComboToday();
      if (Objects.equals(response.agency.id, worldMapDailyCombo.agency.id) && Objects.equals(response.tool.id,
        worldMapDailyCombo.tool.id) || Objects.equals(response.continent.id, worldMapDailyCombo.continent.id)) {
        totalPointWin = totalPointWin.add(worldMapDailyCombo.point);
        sql+= ", winDailyCombo = true";
      }
    }
    sql += " where id = :id and user.id = :userId and mission\" + number + \"IsCompleted = false and numberMissionCompleted + 1 <= 5 and isCompleted = false";
    if (WorldMap.update(sql, parameters) == 1) {
      User.updatePointUser(userId, totalPointWin);
      return true;
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }*/

}

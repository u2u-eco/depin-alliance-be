package xyz.telegram.depinalliance.services;

import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.WorldMapRequest;
import xyz.telegram.depinalliance.common.models.response.WorldMapResponse;
import xyz.telegram.depinalliance.common.models.response.WorldMapResultResponse;
import xyz.telegram.depinalliance.common.utils.Utils;
import xyz.telegram.depinalliance.entities.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author holden on 09-Oct-2024
 */
@ApplicationScoped
public class WorldMapService {

  @Inject
  RedisService redisService;

  @Transactional
  public void newWorldMap(long userId, WorldMapRequest request) {
    if (request == null || StringUtils.isBlank(request.agency) || StringUtils.isBlank(
      request.continent) || StringUtils.isBlank(request.tool)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapResponse response = redisService.findWorldMap(userId);
    if (response != null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemAgency = redisService.findWorldMapByCode(request.agency);
    if (itemAgency == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemContinent = redisService.findWorldMapByCode(request.continent);
    if (itemContinent == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemTool = redisService.findWorldMapByCode(request.tool);
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

    List<WorldMapCity> randomSeries = genWorldMapCity(itemContinent.name);
    //mission 1
    worldMap.mission1Type = Enums.WorldMapType.SUDOKU;
    worldMap.mission1Title = "Play sudoku";
    worldMap.mission1Location = randomSeries.get(0).location;
    worldMap.mission1LocationName = randomSeries.get(0).name;

    //mission 2
    worldMap.mission2Type = Enums.WorldMapType.SUDOKU;
    worldMap.mission2Title = "Play sudoku";
    worldMap.mission2Location = randomSeries.get(1).location;
    worldMap.mission2LocationName = randomSeries.get(1).name;
    //mission 3
    worldMap.mission3Type = Enums.WorldMapType.SUDOKU;
    worldMap.mission3Title = "Play sudoku";
    worldMap.mission3Location = randomSeries.get(2).location;
    worldMap.mission3LocationName = randomSeries.get(2).name;
    //mission 4
    worldMap.mission4Type = Enums.WorldMapType.SUDOKU;
    worldMap.mission4Title = "Play sudoku";
    worldMap.mission4Location = randomSeries.get(3).location;
    worldMap.mission4LocationName = randomSeries.get(3).name;
    //mission 5
    worldMap.mission5Type = Enums.WorldMapType.SUDOKU;
    worldMap.mission5Title = "Play sudoku";
    worldMap.mission5Location = randomSeries.get(4).location;
    worldMap.mission5LocationName = randomSeries.get(4).name;
    worldMap.persist();
  }

  @Transactional
  public void changeItem(long userId, WorldMapRequest request) {
    if (request == null || StringUtils.isBlank(request.agency) || StringUtils.isBlank(
      request.continent) || StringUtils.isBlank(request.tool)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapResponse response = redisService.findWorldMap(userId);
    if (response == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemAgency = redisService.findWorldMapByCode(request.agency);
    if (itemAgency == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemContinent = redisService.findWorldMapByCode(request.continent);
    if (itemContinent == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapItem itemTool = redisService.findWorldMapByCode(request.tool);
    if (itemTool == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Parameters parameters = new Parameters();
    List<String> updateLst = new ArrayList<>();
    if (!Objects.equals(response.continent.id, itemContinent.id)) {
      List<WorldMapCity> randomSeries = genWorldMapCity(itemContinent.name);
      parameters.and("continentId", itemContinent.id);
      updateLst.add("continent.id = :continentId");
      for (int i = 1; i <= randomSeries.size(); i++) {
        WorldMapCity city = randomSeries.get(i - 1);
        String fieldName = "mission" + i;
        updateLst.add(fieldName + "Location = :" + fieldName + "Location");
        parameters.and(fieldName + "Location", city.location);
        updateLst.add(fieldName + "LocationName = :" + fieldName + "LocationName");
        parameters.and(fieldName + "LocationName", city.name);
      }
    }
    if (!Objects.equals(response.agency.id, itemAgency.id)) {
      parameters.and("agencyId", itemAgency.id);
      updateLst.add("agency.id = :agencyId");
    }
    if (!Objects.equals(response.tool.id, itemTool.id)) {
      parameters.and("toolId", itemTool.id);
      updateLst.add("tool.id = :toolId");
    }
    if (!updateLst.isEmpty()) {
      String query = String.join(",", updateLst);
      query += " where id = :id and isCompleted = false";
      parameters.and("id", response.id);
      if (WorldMap.update(query, parameters) == 1) {
        redisService.clearWorldMap(userId);
      }
    }
  }

  @Transactional
  public Object worldMapStart(long userId, int number) {
    WorldMapResponse response = redisService.findWorldMap(userId);
    if (response == null || number > 5 || number <= 0 || response.isCompleted) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapResultResponse resultResponse = response.results.stream()
      .filter(result -> result.id == number && !result.isCompleted).findFirst().orElse(null);
    if (resultResponse == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Parameters parameters = new Parameters();
    String sql = "mission" + number + "Detail = :missionDetail , mission" + number + "CreatedAt = :createdAt where id = :id and user.id = :userId and mission" + number + "IsCompleted = false and isCompleted = false";
    Object res = null;
    if (resultResponse.type == Enums.WorldMapType.SUDOKU) {
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

  public List<WorldMapCity> genWorldMapCity(String name) {
    List<WorldMapCity> worldMapCities = redisService.findWorldMapCityByContinent(name);
    Collections.shuffle(worldMapCities);
    int randomSeriesLength = 5;
    return worldMapCities.subList(0, randomSeriesLength);
  }

  @Transactional
  public boolean worldMapComplete(long userId, int number, Object obj) {
    WorldMapResponse response = redisService.findWorldMap(userId);
    long newDayTime = Utils.getNewDay().getTimeInMillis() / 1000;
    if (response == null || number > 5 || number <= 0 || response.isCompleted || obj == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    WorldMapResultResponse resultResponse = response.results.stream().filter(
        result -> result.id == number && !result.isCompleted && result.createdAt != null && result.createdAt >= newDayTime)
      .findFirst().orElse(null);
    if (resultResponse == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
//    if (resultResponse.type == Enums.WorldMapType.SUDOKU) {
//      if (StringUtils.isBlank(resultResponse.gameDetail)) {
//        throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
//      }
//      MissionSudoku userResult = (MissionSudoku) obj;
//      MissionSudoku missionSudoku = Utils.toObject(resultResponse.gameDetail, MissionSudoku.class);
//      if (userResult.id.compareTo(0L) <= 0 || StringUtils.isBlank(userResult.mission) || !Objects.equals(userResult.id,
//        Objects.requireNonNull(missionSudoku).id)) {
//        throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
//      }
//      if (!userResult.solution.equals(missionSudoku.solution)) {
//        throw new BusinessException(ResponseMessageConstants.MISSION_FAILED);
//      }
//    } else {
//      return false;
//    }
    long currentTime = Utils.getCalendar().getTimeInMillis() / 1000;
    Parameters parameters = new Parameters();
    String sql = "mission" + number + "EndedAt = :updatedAt, mission" + number + "IsCompleted = true, numberMissionCompleted = numberMissionCompleted + 1, mission" + number + "MiningPower = :miningPower, mission" + number + "Tier = :tier ";
    sql += ", mission" + number + "Agency.id = :agency, mission" + number + "Tool.id = :tool, mission" + number + "Continent.id = :continent ";
    String sqlWhere = " where id = :id and user.id = :userId and mission" + number + "IsCompleted = false and numberMissionCompleted + 1 <= 5 and isCompleted = false ";
    parameters.and("id", response.id);
    parameters.and("userId", userId);
    parameters.and("updatedAt", currentTime);
    parameters.and("agency", response.agency.id);
    parameters.and("tool", response.tool.id);
    parameters.and("continent", response.continent.id);
    BigDecimal miningPowerUser = User.findMiningPowerReal(userId);
    parameters.and("miningPower", miningPowerUser);
    WorldMapTier worldMapTier = WorldMapTier.findByMiningPowerFrom(miningPowerUser);
    long tier = worldMapTier.id;
    parameters.and("tier", tier);
    BigDecimal baseReward = new BigDecimal(
      redisService.findConfigByKey(Enums.Config.WORLD_MAP_BASE_REWARD_POINT)).multiply(new BigDecimal(tier));
    BigDecimal winComboReward = BigDecimal.ZERO;
    WorldMapDailyCombo worldMapDailyCombo = redisService.findMWorldMapDailyComboToday();
    BigDecimal percentBonus = BigDecimal.ZERO;
    if (Objects.equals(response.agency.id, worldMapDailyCombo.agency.id)) {
      percentBonus = percentBonus.add(
        new BigDecimal(redisService.findConfigByKey(Enums.Config.WORLD_MAP_BONUS_AGENCY_PERCENT)));
    }
    if (Objects.equals(response.tool.id, worldMapDailyCombo.tool.id)) {
      percentBonus = percentBonus.add(
        new BigDecimal(redisService.findConfigByKey(Enums.Config.WORLD_MAP_BONUS_TOOL_PERCENT)));
    }
    if (Objects.equals(response.continent.id, worldMapDailyCombo.continent.id)) {
      percentBonus = percentBonus.add(
        new BigDecimal(redisService.findConfigByKey(Enums.Config.WORLD_MAP_BONUS_CONTINENT_PERCENT)));
    }
    baseReward = baseReward.add(baseReward.multiply(percentBonus));
    sql += ", mission" + number + "Reward = :baseReward ";
    parameters.and("baseReward", baseReward);

    if (!response.isWinCombo && percentBonus.compareTo(BigDecimal.ONE) == 0) {
      sql += ", winDailyCombo = true, winDailyComboReward = :winDailyComboReward, winDailyComboTier = :tier";
      sqlWhere += " and winDailyCombo = false ";
      winComboReward = new BigDecimal(redisService.findConfigByKey(Enums.Config.WORLD_MAP_DAILY_COMBO_POINT)).multiply(
        new BigDecimal(tier));
      parameters.and("winDailyComboReward", winComboReward);
    }

    if (WorldMap.update(sql + sqlWhere, parameters) == 1) {
      User.updatePointUser(userId, baseReward.add(winComboReward));
      return true;
    }
    throw new BusinessException(ResponseMessageConstants.HAS_ERROR);
  }

}

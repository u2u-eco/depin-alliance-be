package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.LeagueRequest;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.entities.League;
import xyz.telegram.depinalliance.entities.LeagueLevel;
import xyz.telegram.depinalliance.entities.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 28-Aug-2024
 */
@ApplicationScoped
public class LeagueService {

  @Transactional
  public LeagueResponse createLeague(User user, LeagueRequest request) throws BusinessException {
    if (user.league != null || request == null || request.image == null || StringUtils.isBlank(request.name)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (!validateName(request)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League league = new League();
    league.name = request.name;
    league.user = user;
    league.avatar = "";
    league.totalContributors = 1;
    league.totalMining = user.miningPower.multiply(user.rateMining);
    league.level = new LeagueLevel(1L);
    League.createLeague(league);
    Map<String, Object> params = new HashMap<>();
    params.put("league", league.id);
    params.put("id", user.id);
    User.updateUser("league.id = :league where id = :id", params);
    return new LeagueResponse(league, user.code);
  }

  @Transactional
  public LeagueResponse joinLeague(User user, String code) throws BusinessException {
    if (user.league != null || StringUtils.isBlank(code)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League league = League.findByCode(code);
    if (league == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("league", league.id);
    params.put("id", user.id);
    User.updateUser("league.id = :league where id = :id", params);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", league.id);
    leagueParams.put("totalMining", user.miningPower.multiply(user.rateMining));
    League.updateObject(
      "totalContributors = totalContributors + 1, totalMining = totalMining + :totalMining where id = :id",
      leagueParams);
    return new LeagueResponse(league, user.code);
  }

  @Transactional
  public boolean leaveLeague(User user) throws BusinessException {
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (user.league.user.id == user.id) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("league", null);
    params.put("id", user.id);
    User.updateUser("league.id = :league where id = :id", params);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", user.league.id);
    leagueParams.put("totalMining", user.miningPower.multiply(user.rateMining));
    League.updateObject(
      "totalContributors = totalContributors - 1, totalMining = totalMining - :totalMining where id = :id",
      leagueParams);
    return true;
  }

  @Transactional
  public boolean kick(User user, long id) throws BusinessException {
    User userKick = User.findById(id);
    if (userKick == null) {
      throw new BusinessException(ResponseMessageConstants.NOT_FOUND);
    }
    if (user.league == null || userKick.league == null || user.id == id || user.league.user.id != user.id || userKick.league.id != user.league.id) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }

    Map<String, Object> params = new HashMap<>();
    params.put("league", null);
    params.put("id", id);
    User.updateUser("league.id = :league where id = :id", params);
    Map<String, Object> leagueParams = new HashMap<>();
    leagueParams.put("id", user.league.id);
    leagueParams.put("totalMining", userKick.miningPower.multiply(userKick.rateMining));
    League.updateObject(
      "totalContributors = totalContributors - 1, totalMining = totalMining - :totalMining where id = :id",
      leagueParams);
    return true;
  }

  public void updateXp(User user, BigDecimal xp) {
    if (user.league == null || xp == null || xp.compareTo(BigDecimal.ZERO) <= 0) {
      return;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("xp", xp);
    params.put("id", user.league.id);
    League.updateObject("xp = xp + :xp where id = :id", params);
    Long maxLevel = LeagueLevel.maxLevel();
    LeagueLevel level = LeagueLevel.getLevelBeExp(xp);
    if (null != level && level.id - user.level.id > 0 && level.id < maxLevel) {
      League.updateLevel(user.league.id, level.id);
    }
  }

  public boolean validateName(LeagueRequest request) {
    if (request == null || StringUtils.isBlank(request.name) || request.name.trim().length() < 3) {
      return false;
    }
    return League.countByNameNormalize(request.name) <= 0;
  }
}

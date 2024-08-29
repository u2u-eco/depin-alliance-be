package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.LeagueRequest;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.entities.League;
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
    League.updateObject("totalContributors = totalContributors + 1 where id = :id", leagueParams);
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
    League.updateObject("totalContributors = totalContributors - 1 where id = :id", leagueParams);
    return true;
  }

  public boolean validateName(LeagueRequest request) {
    if (request == null || StringUtils.isBlank(request.name) || request.name.trim().length() < 3) {
      return false;
    }
    return League.countByNameNormalize(request.name) <= 0;
  }
}

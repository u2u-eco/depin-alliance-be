package xyz.telegram.depinalliance.resources;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.LeagueRequest;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.common.models.response.RankingResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.entities.League;
import xyz.telegram.depinalliance.entities.LeagueJoinRequest;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.services.LeagueService;
import xyz.telegram.depinalliance.services.RedisService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author holden on 29-Aug-2024
 */
@Path("league")
public class LeagueResource extends BaseResource {

  @Inject
  LeagueService leagueService;
  @Inject
  RedisService redisService;

  @GET
  @Path("")
  public ResponseData getLeague(PagingParameters pagingParameters) {
    return ResponseData.ok(League.findByPaging(pagingParameters));
  }

  @POST
  @Path("")
  public ResponseData createLeague(LeagueRequest request) throws Exception {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(leagueService.createLeague(getUser(), request));
    }
  }

  @GET
  @Path("join/{code}")
  public ResponseData joinLeague(@PathParam("code") String code) throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(leagueService.joinLeague(getUser(), code));
    }
  }

  @GET
  @Path("kick/{id}")
  public ResponseData kick(@PathParam("id") Long id) throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(leagueService.kick(getUser(), id));
    }
  }

  @GET
  @Path("reject/{id}")
  public ResponseData reject(@PathParam("id") Long id) throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(leagueService.reject(getUser(), id));
    }
  }

  @GET
  @Path("approve/{id}")
  public ResponseData approve(@PathParam("id") Long id) throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(leagueService.approve(getUser(), id));
    }
  }

  @GET
  @Path("cancel")
  public ResponseData cancel() throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(leagueService.cancel(getUser()));
    }
  }

  @GET
  @Path("leave")
  public ResponseData leaveLeague() throws BusinessException {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(leagueService.leaveLeague(getUser()));
    }
  }

  @POST
  @Path("validate-name")
  public ResponseData validateName(LeagueRequest request) {
    synchronized (getTelegramId().toString().intern()) {
      return ResponseData.ok(leagueService.validateName(request));
    }
  }

  @GET
  @Path("user-league")
  public ResponseData getUserLeague() throws BusinessException {
    User user = getUser();
    League userLeague = user.league;
    if (userLeague == null) {
      LeagueJoinRequest requestCheck = LeagueJoinRequest.findPendingByUser(user.id);
      if (requestCheck != null) {
        return ResponseData.ok(new LeagueResponse(requestCheck.league, true));
      }
    }
    return ResponseData.ok(userLeague == null ? "" : new LeagueResponse(userLeague, user));
  }

  @GET
  @Path("member")
  public ResponseData getMember(PagingParameters pagingParameters, @QueryParam("username") String username,
    @QueryParam("is-ranking") boolean isRanking) throws BusinessException {
    User user = getUser();
    if (user.league == null) {
      return ResponseData.ok();
    }
    long leagueId = user.league.id;
    if (isRanking) {
      Map<String, Object> res = new HashMap<>();
      res.put("currentRank", User.find(
        "select position from ( select id as id, row_number() over(order by miningPowerReal desc, createdAt asc) as position from User where id != 1 and league.id = :leagueId) result where id = :userId",
        Parameters.with("userId", user.id).and("leagueId", leagueId)).project(Long.class).firstResult());
      res.put("ranking", User.find("id != 1 and league.id = :leagueId",
          Sort.descending("miningPowerReal").and("createdAt", Sort.Direction.Ascending), leagueId).page(0, 30)
        .project(RankingResponse.class).list());
      return ResponseData.ok(res);
    }
    if (StringUtils.isBlank(pagingParameters.sortBy)) {
      pagingParameters.sortBy = "createdAt";
      pagingParameters.sortAscending = true;
    }
    return ResponseData.ok(User.findMemberLeagueByUserAndPaging(pagingParameters, leagueId, username));
  }

  @GET
  @Path("join-request")
  public ResponseData getJoinRequest(PagingParameters pagingParameters, @QueryParam("username") String username)
    throws BusinessException {
    User user = getUser();
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League userLeague = redisService.findLeagueById(user.league.id);
    if (!Objects.equals(userLeague.user.id, user.id)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    if (StringUtils.isBlank(pagingParameters.sortBy)) {
      pagingParameters.sortBy = "createdAt";
    }
    return ResponseData.ok(LeagueJoinRequest.findPendingByPagingAndLeagueId(pagingParameters, userLeague.id, username));
  }

  @GET
  @Path("total-join-request")
  public ResponseData getTotalJoinRequest() throws BusinessException {
    User user = getUser();
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League userLeague = redisService.findLeagueById(user.league.id);
    if (!Objects.equals(userLeague.user.id, user.id)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("leagueId", userLeague.id);
    params.put("status", Enums.LeagueJoinRequestStatus.PENDING);
    return ResponseData.ok(LeagueJoinRequest.count("league.id = :leagueId and status = :status", params));
  }
}

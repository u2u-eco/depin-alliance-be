package xyz.telegram.depinalliance.resources;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.LeagueRequest;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.common.models.response.RankingResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.entities.League;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.services.LeagueService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 29-Aug-2024
 */
@Path("league")
public class LeagueResource extends BaseResource {

  @Inject
  LeagueService leagueService;

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
    return ResponseData.ok(userLeague == null ? "" : new LeagueResponse(userLeague, user));
  }

  @GET
  @Path("member")
  public ResponseData getMember(PagingParameters pagingParameters, @QueryParam("is-ranking") boolean isRanking)
    throws BusinessException {
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
    return ResponseData.ok();
  }
}

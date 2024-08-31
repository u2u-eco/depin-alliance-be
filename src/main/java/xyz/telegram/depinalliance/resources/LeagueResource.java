package xyz.telegram.depinalliance.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.LeagueRequest;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.entities.League;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.services.LeagueService;

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
  public ResponseData createLeague(LeagueRequest request) throws BusinessException {
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
}

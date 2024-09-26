package xyz.telegram.depinalliance.resources;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.*;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.common.models.response.RankingResponse;
import xyz.telegram.depinalliance.common.models.response.ResponseData;
import xyz.telegram.depinalliance.entities.League;
import xyz.telegram.depinalliance.entities.LeagueJoinRequest;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.services.LeagueService;
import xyz.telegram.depinalliance.services.RedisService;

import java.util.HashMap;
import java.util.List;
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
  public ResponseData getLeague(PagingParameters pagingParameters, @QueryParam("name") String nameSearch) {
    return ResponseData.ok(League.findByPagingAndNameSearchAndUserId(pagingParameters, nameSearch, getTelegramId()));
  }

  @POST
  @Path("")
  public ResponseData createLeague(LeagueRequest request) throws Exception {
    return ResponseData.ok(leagueService.createLeague(getUser(), request));
  }

  @POST
  @Path("avatar")
  public ResponseData changeAvatar(LeagueRequest request) throws Exception {
    return ResponseData.ok(leagueService.changeAvatar(getUser(), request));
  }

  @GET
  @Path("join/{code}")
  public ResponseData joinLeague(@PathParam("code") String code) throws BusinessException {
    return ResponseData.ok(leagueService.joinLeague(getUser(), code));
  }

  @GET
  @Path("kick/{id}")
  public ResponseData kick(@PathParam("id") Long id) throws BusinessException {
    return ResponseData.ok(leagueService.kick(getUser(), id));
  }

  @GET
  @Path("reject/{id}")
  public ResponseData reject(@PathParam("id") Long id) throws BusinessException {
    return ResponseData.ok(leagueService.reject(getUser(), id));
  }

  @GET
  @Path("approve/{id}")
  public ResponseData approve(@PathParam("id") Long id) throws BusinessException {
    return ResponseData.ok(leagueService.approve(getUser(), id));
  }

  @GET
  @Path("cancel/{code}")
  public ResponseData cancel(@PathParam("code") String code) throws BusinessException {
    return ResponseData.ok(leagueService.cancel(getUser(), code));
  }

  @GET
  @Path("leave")
  public ResponseData leaveLeague() throws BusinessException {
    return ResponseData.ok(leagueService.leaveLeague(getTelegramId()));
  }

  @POST
  @Path("validate-name")
  public ResponseData validateName(LeagueRequest request) {
    return ResponseData.ok(leagueService.validateName(request));
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
    League userLeague = redisService.findLeagueById(user.league.id, true);
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
    return ResponseData.ok(
      User.findMemberLeagueByLeagueAndUserName(pagingParameters, leagueId, username, userLeague.user.id));
  }

  @GET
  @Path("join-request")
  public ResponseData getJoinRequest(PagingParameters pagingParameters, @QueryParam("username") String username)
    throws BusinessException {
    User user = getUser();
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    League userLeague = redisService.findLeagueById(user.league.id, true);
    List<Long> admins = redisService.findListAdminLeagueByRoleAndLeague(user.league.id, Enums.LeagueRole.ADMIN_REQUEST);
    if (!Objects.equals(userLeague.user.id, user.id) && !admins.contains(user.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
    }
    if (StringUtils.isBlank(pagingParameters.sortBy)) {
      pagingParameters.sortBy = "createdAt";
      pagingParameters.sortAscending = true;
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
    League userLeague = redisService.findLeagueById(user.league.id, true);
    if (!Objects.equals(userLeague.user.id, user.id)) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("leagueId", userLeague.id);
    params.put("status", Enums.LeagueJoinRequestStatus.PENDING);
    return ResponseData.ok(LeagueJoinRequest.count("league.id = :leagueId and status = :status", params));
  }

  @POST
  @Path("funding")
  public ResponseData funding(FundRequest request) {
    return ResponseData.ok(leagueService.fund(getUser(), request));
  }

  @POST
  @Path("contribute")
  public ResponseData contribute(ContributeItemRequest request) {
    return ResponseData.ok(leagueService.contribute(getUser(), request));
  }

  @POST
  @Path("role")
  public ResponseData updateRole(LeagueRoleRequest request) {
    return ResponseData.ok(leagueService.updateRole(getUser(), request));
  }
}

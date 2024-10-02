package xyz.telegram.depinalliance.resources;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.constans.ResponseMessageConstants;
import xyz.telegram.depinalliance.common.exceptions.BusinessException;
import xyz.telegram.depinalliance.common.models.request.*;
import xyz.telegram.depinalliance.common.models.response.*;
import xyz.telegram.depinalliance.entities.League;
import xyz.telegram.depinalliance.entities.LeagueJoinRequest;
import xyz.telegram.depinalliance.entities.LeagueMember;
import xyz.telegram.depinalliance.entities.User;
import xyz.telegram.depinalliance.services.LeagueService;
import xyz.telegram.depinalliance.services.RedisService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  @GET
  @Path("all")
  public ResponseData getAllLeague(PagingParameters pagingParameters, @QueryParam("name") String nameSearch) {
    return ResponseData.ok(League.findByPagingAndNameSearch(pagingParameters, nameSearch));
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
    LeagueMember leagueMember = redisService.findLeagueMemberByUserId(getTelegramId());
    if (leagueMember == null) {
      LeagueJoinRequest requestCheck = LeagueJoinRequest.findPendingByUser(getTelegramId());
      if (requestCheck != null) {
        return ResponseData.ok(new LeagueResponse(requestCheck.league, true));
      } else {
        return ResponseData.ok("");
      }
    }
    return ResponseData.ok(
      new LeagueResponse(League.findById(leagueMember.league.id), getUser(), leagueMember.leagueRole));
  }

  @GET
  @Path("user-league-current-rank")
  public ResponseData getUserLeagueCurrentRank() throws BusinessException {
    LeagueMember leagueMember = redisService.findLeagueMemberByUserId(getTelegramId());
    if (leagueMember == null) {
      return ResponseData.ok("");
    }
    return ResponseData.ok(League.find(
      "select position from ( select id as id, row_number() over(order by profit desc, totalContributors desc, createdAt asc) as position from League) result where id = :leagueId",
      Parameters.with("leagueId", leagueMember.league.id)).project(Long.class).firstResult());
  }

  @GET
  @Path("member")
  public ResponseData getMember(PagingParameters pagingParameters, @QueryParam("username") String username,
    @QueryParam("is-funding") boolean isFunding) throws BusinessException {
    User user = getUser();
    if (user.league == null) {
      return ResponseData.ok();
    }
    League userLeague = redisService.findLeagueById(user.league.id, true);
    User userAdmin = User.findById(userLeague.user.id);
    MemberLeagueResponse admin = new MemberLeagueResponse(userAdmin.id, userAdmin.username, userAdmin.avatar,
      userAdmin.miningPowerReal);
    Map<String, Object> res = new HashMap<>();
    res.put("admin", admin);
    LeagueMember leagueMember = redisService.findLeagueMemberByUserId(user.id);

    List<Long> admins = redisService.findListAdminLeagueByRoleAndLeague(user.league.id, Enums.LeagueRole.ADMIN_KICK);
    if (isFunding) {
      res.put("currentMember",
        new MemberLeagueResponse(user.id, user.username, user.avatar, leagueMember.pointFunding));
      res.put("currentRank", LeagueMember.find(
        "select position from ( select user.id as id, row_number() over(order by pointFunding desc, createdAt asc) as position from LeagueMember where league.id = :leagueId) result where id = :userId",
        Parameters.with("userId", user.id).and("leagueId", userLeague.id)).project(Long.class).firstResult());
    } else {
      res.put("currentMember",
        new MemberLeagueResponse(user.id, user.username, user.avatar, leagueMember.contributeProfit));
      res.put("currentRank", LeagueMember.find(
        "select position from ( select user.id as id, row_number() over(order by contributeProfit desc, createdAt asc) as position from LeagueMember where league.id = :leagueId) result where id = :userId",
        Parameters.with("userId", user.id).and("leagueId", userLeague.id)).project(Long.class).firstResult());
    }
    if (!admins.contains(user.id)) {
      //member
      if (isFunding) {
        res.put("ranking", LeagueMember.find(
            "select user.id, user.username, user.avatar, pointFunding from LeagueMember lm where league.id = ?1",
            Sort.descending("pointFunding").and("lm.createdAt", Sort.Direction.Ascending), userLeague.id).page(0, 30)
          .project(MemberLeagueResponse.class).list());
        return ResponseData.ok(res);
      } else {
        res.put("ranking", LeagueMember.find(
            "select user.id, user.username, user.avatar, contributeProfit from LeagueMember lm where league.id = ?1",
            Sort.descending("contributeProfit").and("lm.createdAt", Sort.Direction.Ascending), userLeague.id).page(0, 30)
          .project(MemberLeagueResponse.class).list());
        return ResponseData.ok(res);
      }
    } else {
      //admin
      if (isFunding) {
        String sql = "select user.id, user.username, user.avatar, pointFunding from LeagueMember lm where league.id = :leagueId";
        Map<String, Object> params = new HashMap<>();
        params.put("leagueId", userLeague.id);
        if (StringUtils.isNotBlank(username)) {
          params.put("username", "%" + username.toLowerCase().trim() + "%");
          sql += " and lower(user.username) like :username";
        }
        PanacheQuery<PanacheEntityBase> panacheQuery = LeagueMember.find(sql,
          Sort.descending("pointFunding").and("lm.createdAt", Sort.Direction.Ascending), params);
        res.put("ranking", panacheQuery.page(pagingParameters.getPage()).project(MemberLeagueResponse.class).list());
        ResponsePage responsePage = new ResponsePage(new ArrayList(), pagingParameters, panacheQuery.count());

        return ResponseData.ok(res,
          new Pagination(pagingParameters.page, pagingParameters.size, responsePage.getTotalPages(),
            responsePage.total));
      } else {
        String sql = "select user.id, user.username, user.avatar, contributeProfit from LeagueMember lm where league.id = :leagueId";
        Map<String, Object> params = new HashMap<>();
        params.put("leagueId", userLeague.id);
        if (StringUtils.isNotBlank(username)) {
          params.put("username", "%" + username.toLowerCase().trim() + "%");
          sql += " and lower(user.username) like :username";
        }
        PanacheQuery<PanacheEntityBase> panacheQuery = LeagueMember.find(sql,
          Sort.descending("contributeProfit").and("lm.createdAt", Sort.Direction.Ascending), params);
        res.put("ranking", panacheQuery.page(pagingParameters.getPage()).project(MemberLeagueResponse.class).list());
        ResponsePage responsePage = new ResponsePage(new ArrayList(), pagingParameters, panacheQuery.count());
        return ResponseData.ok(res,
          new Pagination(pagingParameters.page, pagingParameters.size, responsePage.getTotalPages(),
            responsePage.total));
      }
    }
  }

  @GET
  @Path("join-request")
  public ResponseData getJoinRequest(PagingParameters pagingParameters, @QueryParam("username") String username)
    throws BusinessException {
    User user = getUser();
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    List<Long> admins = redisService.findListAdminLeagueByRoleAndLeague(user.league.id, Enums.LeagueRole.ADMIN_REQUEST);
    if (!admins.contains(user.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
    }
    if (StringUtils.isBlank(pagingParameters.sortBy)) {
      pagingParameters.sortBy = "createdAt";
      pagingParameters.sortAscending = true;
    }
    return ResponseData.ok(
      LeagueJoinRequest.findPendingByPagingAndLeagueId(pagingParameters, user.league.id, username));
  }

  @GET
  @Path("total-join-request")
  public ResponseData getTotalJoinRequest() throws BusinessException {
    User user = getUser();
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    List<Long> admins = redisService.findListAdminLeagueByRoleAndLeague(user.league.id, Enums.LeagueRole.ADMIN_REQUEST);
    if (!admins.contains(user.id)) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_ROLE_INVALID);
    }
    Map<String, Object> params = new HashMap<>();
    params.put("leagueId", user.league.id);
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

  @GET
  @Path("detail-member/{id}")
  public ResponseData detailMember(@PathParam("id") Long id) {
    User user = getUser();
    if (user.league == null) {
      throw new BusinessException(ResponseMessageConstants.DATA_INVALID);
    }
    MemberLeagueDetail leagueMember = LeagueMember.findByUserIdAndLeagueId(id, user.league.id);
    if (leagueMember == null) {
      throw new BusinessException(ResponseMessageConstants.LEAGUE_MEMBER_NOT_EXIST);
    }
    return ResponseData.ok(leagueMember);
  }

  @GET
  @Path("detail-league/{code}")
  public ResponseData getLeague(@PathParam("code") String code) {
    League league = League.findByCode(code);
    if (league == null) {
      return ResponseData.ok();
    }
    return ResponseData.ok(
      new LeagueResponse(league.name, league.avatar, league.totalContributors, league.profit, league.point, League.find(
        "select position from ( select id as id, row_number() over(order by profit desc, totalContributors desc, createdAt asc) as position from League) result where id = :leagueId",
        Parameters.with("leagueId", league.id)).project(Long.class).firstResult()));
  }
}

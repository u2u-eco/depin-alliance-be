package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.LeagueJoinResponse;
import xyz.telegram.depinalliance.common.models.response.ResponsePage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author holden on 18-Sep-2024
 */

@Entity
@Table(name = "league_join_requests")
public class LeagueJoinRequest extends BaseEntity {
  @Id
  @SequenceGenerator(name = "leagueJoinRequestSequence", sequenceName = "league_join_request_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leagueJoinRequestSequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  public League league;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  public Enums.LeagueJoinRequestStatus status;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_action_id")
  public User userAction;
  @Column(unique = true)
  public String hash;

  public static LeagueJoinRequest findPendingByUser(Long userId) {
    return find("user.id = ?1 and status = ?2", userId, Enums.LeagueJoinRequestStatus.PENDING).firstResult();
  }

  public static LeagueJoinRequest findPendingByUserAndLeague(Long userId, Long leagueId) {
    return find("user.id = ?1 and status = ?2 and league.id = ?3", userId, Enums.LeagueJoinRequestStatus.PENDING,
      leagueId).firstResult();
  }

  public static ResponsePage<LeagueJoinResponse> findPendingByPagingAndLeagueId(PagingParameters pageable,
    long leagueId, String username) {
    String sql = "select user.username, user.id, user.miningPowerReal, user.avatar from LeagueJoinRequest where league.id = :leagueId and status = :status";
    Map<String, Object> params = new HashMap<>();
    params.put("leagueId", leagueId);
    params.put("status", Enums.LeagueJoinRequestStatus.PENDING);
    if (StringUtils.isNotBlank(username)) {
      params.put("username", "%" + username.toLowerCase().trim() + "%");
      sql += " and lower(user.username) like :username";
    }
    PanacheQuery<PanacheEntityBase> panacheQuery = find(sql, pageable.getSort(), params);
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(LeagueJoinResponse.class).list(), pageable,
      panacheQuery.count());
  }
}

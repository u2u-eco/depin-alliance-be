package xyz.telegram.depinalliance.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import org.apache.commons.lang3.RandomStringUtils;
import xyz.telegram.depinalliance.common.models.request.PagingParameters;
import xyz.telegram.depinalliance.common.models.response.LeagueResponse;
import xyz.telegram.depinalliance.common.models.response.ResponsePage;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author holden on 28-Aug-2024
 */
@Entity
@Table(name = "league")
public class League extends BaseEntity {
  @Id
  @SequenceGenerator(name = "leagueSequence", sequenceName = "league_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leagueSequence")
  public Long id;
  public String name;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true)
  public User user;
  @Column(unique = true)
  public String code;
  public String avatar;
  @Column(unique = true)
  public String nameNormalize;
  @Column(name = "total_contributors")
  public long totalContributors = 0;
  @Column(name = "total_mining")
  public BigDecimal totalMining = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;

  public static League createLeague(League league) {
    league.create();
    league.nameNormalize = league.name.toLowerCase();
    league.code = getCodeUser();
    league.persistAndFlush();
    return league;
  }

  public static int updateObject(String query, Map<String, Object> params) {
    return update(query, params);
  }

  public static String getCodeUser() {
    while (true) {
      String code = RandomStringUtils.randomAlphanumeric(6);
      if (countByCode(code) <= 0)
        return code;
    }
  }

  public static League findByCode(String code) {
    return find("code", code).firstResult();
  }

  public static long countByCode(String code) {
    return count("code", code);
  }

  public static long countByNameNormalize(String nameNormalize) {
    return count("nameNormalize", nameNormalize.toLowerCase());
  }

  public static ResponsePage<LeagueResponse> findByPaging(PagingParameters pageable) {
    PanacheQuery<PanacheEntityBase> panacheQuery = find(
      "select code, name, avatar, totalContributors, totalMining from League", Sort.descending("totalMining","xp"));
    return new ResponsePage<>(panacheQuery.page(pageable.getPage()).project(LeagueResponse.class).list(), pageable,
      panacheQuery.count());
  }

  public static LeagueResponse findDetailById(long id) {
    return find("select code, name, avatar, totalContributors, totalMining from League where id = ?1", id).project(
      LeagueResponse.class).firstResult();
  }
}

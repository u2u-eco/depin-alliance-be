package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;

/**
 * @author holden on 24-Sep-2024
 */
//@Entity
//@Table(name = "league_contribute_histories")
public class LeagueContributeHistory extends BaseEntity {
  @Id
  @SequenceGenerator(name = "leagueContributeHistorySequence", sequenceName = "league_contribute_history_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leagueContributeHistorySequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true)
  public User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id", unique = true)
  public League league;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_item_id", unique = true)
  public UserItem userItem;
}

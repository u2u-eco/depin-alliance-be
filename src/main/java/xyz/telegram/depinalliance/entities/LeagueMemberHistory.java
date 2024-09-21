package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

/**
 * @author holden on 20-Sep-2024
 */
@Entity
@Table(name = "league_member_histories")
public class LeagueMemberHistory extends BaseEntity {
  @Id
  @SequenceGenerator(name = "leagueMemberHistorySequence", sequenceName = "league_member_history_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leagueMemberHistorySequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id")
  public League league;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_action_id")
  public User userAction;
  public Enums.LeagueMemberType type;

  public static LeagueMemberHistory create(League league, User user, User userAction, Enums.LeagueMemberType type) {
    LeagueMemberHistory leagueMemberHistory = new LeagueMemberHistory();
    leagueMemberHistory.league = league;
    leagueMemberHistory.user = user;
    leagueMemberHistory.userAction = userAction;
    leagueMemberHistory.type = type;
    leagueMemberHistory.create();
    leagueMemberHistory.persist();
    return leagueMemberHistory;
  }
}

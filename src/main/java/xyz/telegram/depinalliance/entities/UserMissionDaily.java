package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

import java.util.Map;

/**
 * @author holden on 28-Aug-2024
 */
@Entity
@Table(name = "user_mission_daily", uniqueConstraints = {
  @UniqueConstraint(columnNames = { "user_id", "mission_id" }) })
public class UserMissionDaily extends BaseEntity {
  @Id
  @SequenceGenerator(name = "userMissionDailySequence", sequenceName = "user_mission_daily_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userMissionDailySequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @Column(name = "twitter_uid")
  public Long twitterUid;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mission_id")
  public MissionDaily mission;
  public Enums.MissionStatus status;

  public static UserMissionDaily create(UserMissionDaily userMission) {
    userMission.create();
    userMission.persist();
    return userMission;
  }

  public static boolean updateObject(String query, Map<String, Object> params) {
    return update(query, params) > 0;
  }

}

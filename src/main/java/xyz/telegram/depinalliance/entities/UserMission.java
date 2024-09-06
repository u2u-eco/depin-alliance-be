package xyz.telegram.depinalliance.entities;

import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;

import java.util.Map;

/**
 * @author holden on 28-Aug-2024
 */
@Entity
@Table(name = "user_missions", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "mission_id" }) })
public class UserMission extends BaseEntity {
  @Id
  @SequenceGenerator(name = "userMissionSequence", sequenceName = "user_mission_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userMissionSequence")
  public Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mission_id")
  public Mission mission;
  public Enums.MissionStatus status;

  public static UserMission create(UserMission userMission) {
    userMission.create();
    userMission.persist();
    return userMission;
  }

  public static boolean updateObject(String query, Map<String, Object> params) {
    return update(query, params) > 0;
  }

}

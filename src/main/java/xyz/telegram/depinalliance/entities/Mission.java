package xyz.telegram.depinalliance.entities;

import io.quarkus.panache.common.Sort;
import jakarta.persistence.*;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.models.response.UserMissionResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author holden on 26-Aug-2024
 */
@Entity
@Table(name = "missions")
public class Mission extends BaseEntity {
  @Id
  @SequenceGenerator(name = "missionSequence", sequenceName = "mission_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "missionSequence")
  public Long id;
  @Column(name = "group_mission")
  public String groupMission;
  public String image;
  public String name;
  public String description;
  public Enums.MissionType type;
  public String url;
  public int orders = 0;
  @Column(name = "is_fake")
  public boolean isFake = true;
  @Column(name = "point", scale = 18, precision = 29)
  public BigDecimal point = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;

  public Mission(Long id) {
    this.id = id;
  }

  public Mission() {
  }

  public static List<UserMissionResponse> findByUserId(long userId) {
    return find(
      "select m.id, m.groupMission, m.name, m.image, m.description, m.type, m.url, m.point, m.xp, um.status from Mission m left join UserMission um on m.id = um.mission.id and um.user.id =?1",
      Sort.ascending("m.orders"), userId).project(UserMissionResponse.class).list();
  }

  public static UserMissionResponse findByUserIdAndMissionId(long userId, long missionId) {
    return find(
      "select m.id, m.groupMission, m.name, m.image, m.description, m.type, m.url, m.point, m.xp, um.status from Mission m left join UserMission um on m.id = um.mission.id and um.user.id =?1 where m.id = ?2",
      Sort.ascending("m.orders"), userId, missionId).project(UserMissionResponse.class).firstResult();
  }
}

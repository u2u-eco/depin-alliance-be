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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "partner_id")
  public Partner partner;
  public String image;
  public String name;
  @Column(columnDefinition = "text")
  public String description;
  public Enums.MissionType type;
  @Column(name = "mission_require")
  public Enums.MissionRequire missionRequire;
  public String url;
  public int orders = 0;
  @Column(name = "is_fake")
  public boolean isFake = true;
  @Column(name = "point", scale = 18, precision = 29)
  public BigDecimal point = BigDecimal.ZERO;
  @Column(name = "xp", scale = 18, precision = 29)
  public BigDecimal xp = BigDecimal.ZERO;
  @Column(name = "box")
  public Long box = 0L;
  @Column(name = "is_active")
  public boolean isActive = true;
  @Column(name = "refer_id")
  public String referId;

  public Mission(Long id) {
    this.id = id;
  }

  public Mission() {
  }

  public static Mission findByMissionRequire(Enums.MissionRequire missionRequire) {
    return find("missionRequire = ?1 and isActive = true", missionRequire).firstResult();
  }

  public static List<UserMissionResponse> findByUserId(long userId, boolean isPartner) {
    return find(
      "select m.id, m.groupMission, m.name, m.image, m.description, m.type, m.url, m.point, m.xp, um.status, m.isFake, m.missionRequire, m.box, m.referId, m.partner.id from Mission m left join UserMission um on m.id = um.mission.id and um.user.id =?1 where m.type != ?2 and m.isActive = true" + (
        isPartner ?
          " and partner is not null" :
          " and partner is null"), Sort.ascending("m.orders"), userId, Enums.MissionType.ON_TIME_IN_APP).project(
      UserMissionResponse.class).list();
  }

  public static List<UserMissionResponse> findTypeOnTimeInAppByUserId(long userId) {
    return find(
      "select m.id, m.groupMission, m.name, m.image, m.description, m.type, m.url, m.point, m.xp, um.status, m.isFake, m.missionRequire, m.box, m.referId, m.partner.id from Mission m left join UserMission um on m.id = um.mission.id and um.user.id =?1 where m.type = ?2 and m.isActive = true and (um.status is null or um.status != ?3) ",
      Sort.ascending("m.orders"), userId, Enums.MissionType.ON_TIME_IN_APP, Enums.MissionStatus.CLAIMED).project(
      UserMissionResponse.class).list();
  }

  public static UserMissionResponse findByUserIdAndMissionId(long userId, long missionId) {
    return find(
      "select m.id, m.groupMission, m.name, m.image, m.description, m.type, m.url, m.point, m.xp, um.status, m.isFake, m.missionRequire, m.box, m.referId, m.partner.id from Mission m left join UserMission um on m.id = um.mission.id and um.user.id =?1 where m.id = ?2 and m.isActive = true",
      Sort.ascending("m.orders"), userId, missionId).project(UserMissionResponse.class).firstResult();
  }
}

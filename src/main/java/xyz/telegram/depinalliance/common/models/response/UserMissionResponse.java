package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.common.utils.Utils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author holden on 28-Aug-2024
 */
public class UserMissionResponse {
  public Long id;
  @JsonIgnore
  public String groupMission;
  public String name;
  public String image;
  public String description;
  public Enums.MissionType type;
  public String url;
  public BigDecimal point;
  public BigDecimal xp;
  public Enums.MissionStatus status;
  @JsonIgnore
  public Boolean isFake;
  @JsonIgnore
  public Enums.MissionRequire missionRequire;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Long amount;
  @JsonIgnore
  public String referId;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public List<QuizResponse> quizArrays;
  @JsonIgnore
  public Long partnerId;
  @JsonIgnore
  public Enums.MissionRewardType rewardType;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String rewardName;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String rewardImage;
  @JsonIgnore
  public Long userMissionId;

  public UserMissionResponse(Long id, String groupMission, String name, String image, String description,
    Enums.MissionType type, String url, BigDecimal point, BigDecimal xp, Enums.MissionStatus status, Boolean isFake,
    Enums.MissionRequire missionRequire, Long amount, String referId, Long partnerId,
    Enums.MissionRewardType rewardType, String rewardImage) {
    this.groupMission = groupMission;
    this.name = name;
    this.description = description;
    this.type = type;
    this.url = url;
    this.point = Utils.stripDecimalZeros(point);
    this.xp = Utils.stripDecimalZeros(xp);
    this.status = status;
    this.id = id;
    this.image = image;
    this.isFake = isFake;
    this.missionRequire = missionRequire;
    this.amount = amount;
    this.referId = referId;
    this.partnerId = partnerId;
    if (type == Enums.MissionType.QUIZ) {
      try {
        this.quizArrays = Utils.mapToList(description, QuizResponse.class);
      } catch (Exception e) {

      }
      this.description = "";
    }
    this.rewardType = rewardType;
    this.rewardImage = rewardImage;
    if (rewardType != null) {
      this.rewardName = switch (rewardType) {
        case CYBER_BOX -> "box";
        case OPEN_MESH -> "OPEN";
        default -> "";
      };
    }
  }

  public UserMissionResponse(Long id, String groupMission, String name, String image, String description,
    Enums.MissionType type, String url, BigDecimal point, BigDecimal xp, Enums.MissionStatus status, Boolean isFake,
    Enums.MissionRequire missionRequire, Long amount, String referId, Long partnerId,
    Enums.MissionRewardType rewardType, String rewardImage, Long userMissionId) {
    this.groupMission = groupMission;
    this.name = name;
    this.description = description;
    this.type = type;
    this.url = url;
    this.point = Utils.stripDecimalZeros(point);
    this.xp = Utils.stripDecimalZeros(xp);
    this.status = status;
    this.id = id;
    this.image = image;
    this.isFake = isFake;
    this.missionRequire = missionRequire;
    this.amount = amount;
    this.referId = referId;
    this.partnerId = partnerId;
    if (type == Enums.MissionType.QUIZ) {
      try {
        this.quizArrays = Utils.mapToList(description, QuizResponse.class);
      } catch (Exception e) {

      }
      this.description = "";
    }
    this.rewardType = rewardType;
    this.rewardImage = rewardImage;
    if (rewardType != null) {
      this.rewardName = switch (rewardType) {
        case CYBER_BOX -> "box";
        case OPEN_MESH -> "OPEN";
        default -> "";
      };
    }
    this.userMissionId = userMissionId;
  }
}

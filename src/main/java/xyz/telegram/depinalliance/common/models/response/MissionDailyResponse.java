package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author holden on 07-Oct-2024
 */
public class MissionDailyResponse {
  @JsonIgnore
  public Long id;
  public MissionItemResponse agency;
  public MissionItemResponse tool;
  public MissionItemResponse continent;
  public BigDecimal currentRewardPoint = BigDecimal.ZERO;
  public BigDecimal dailyReward;
  public int numberMissionCompleted;
  public boolean isCompleted = false;
  public Long date;
  public long time;

  public List<MissionResultResponse> results = new ArrayList<>();
}

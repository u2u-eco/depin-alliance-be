package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * @author holden on 07-Oct-2024
 */
public class WorldMapResponse {
  @JsonIgnore
  public Long id;
  public WorldMapItemResponse agency;
  public WorldMapItemResponse tool;
  public WorldMapItemResponse continent;
  public int numberMissionCompleted;
  public boolean isCompleted = false;
  public boolean isWinCombo = false;
  public Long date;
  public long time;

  public List<WorldMapResultResponse> results = new ArrayList<>();
}

package xyz.telegram.depinalliance.common.models.response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author holden on 28-Aug-2024
 */
public class GroupMissionResponse {
  public String group;
  public List<UserMissionResponse> missions = new ArrayList<>();
}

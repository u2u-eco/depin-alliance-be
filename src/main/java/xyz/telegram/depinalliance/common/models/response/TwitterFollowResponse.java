package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author holden on 05-Aug-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterFollowResponse {
  public List<TwitterUser> results;
  @JsonProperty("continuation_token")
  public String continuationToken;

  public static class TwitterUser {
    @JsonProperty("user_id")
    public String userId;
  }
}

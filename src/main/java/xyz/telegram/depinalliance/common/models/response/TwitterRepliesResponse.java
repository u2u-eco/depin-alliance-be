package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author holden on 10-Oct-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterRepliesResponse {
  @JsonProperty("continuation_token")
  public String continuationToken;
  public List<TwitterReplies> replies;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TwitterReplies {
    public TwitterUser user;
    @JsonProperty("creation_date")
    public String creationDate;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TwitterUser {
    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("username")
    public String username;
  }
}

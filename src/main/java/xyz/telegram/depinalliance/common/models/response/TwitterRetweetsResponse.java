package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author holden on 10-Oct-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterRetweetsResponse {
  @JsonProperty("continuation_token")
  public String continuationToken;
  public List<TwitterRetweets> retweets;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class TwitterRetweets {
    @JsonProperty("user_id")
    public String userId;
  }
}

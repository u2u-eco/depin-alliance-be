package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author holden on 15-Oct-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterAccessTokenResponse {
  @JsonProperty("access_token")
  public String accessToken;
}

package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author holden on 14-Aug-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTelegramResponse {
  public Long id;
  @JsonProperty("first_name")
  public String firstName;
  @JsonProperty("last_name")
  public String lastName;
  public String username;
}

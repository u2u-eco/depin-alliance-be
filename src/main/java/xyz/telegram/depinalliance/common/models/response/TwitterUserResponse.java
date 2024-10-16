package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author holden on 15-Oct-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterUserResponse {
  public String id;
  public String username;
  public String name;
}

package xyz.telegram.depinalliance.common.models.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author holden on 09-Sep-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {
  public String detectedModel;
  public String platform;
  public ClientHints clientHints;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ClientHints {
    public String model;
  }
}

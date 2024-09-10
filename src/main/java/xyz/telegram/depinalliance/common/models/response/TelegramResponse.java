package xyz.telegram.depinalliance.common.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author holden on 01-Aug-2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramResponse {
  public boolean ok;
  public ChatMember result;

  public static class ChatMember {
    public String status;
  }
}

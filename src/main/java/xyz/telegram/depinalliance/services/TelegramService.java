package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import xyz.telegram.depinalliance.common.models.response.TelegramResponse;
import xyz.telegram.depinalliance.common.models.response.UserTelegramResponse;
import xyz.telegram.depinalliance.common.utils.Utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author holden on 26-Jul-2024
 */
@ApplicationScoped
public class TelegramService {

  @ConfigProperty(name = "telegram.token")
  String botToken;
  @ConfigProperty(name = "telegram.token.verify")
  String botTokenVerify;
  @ConfigProperty(name = "telegram.validate")
  boolean isValidate;
  @ConfigProperty(name = "login.time-out")
  long timeOut;
  @RestClient
  TelegramClient telegramClient;

  public UserTelegramResponse validateInitData(String initData) {
    Map<String, String> params = new TreeMap<>();
    String[] pairs = initData.split("&");
    String receivedHash = null;
    String userStr = null;
    String authDateStr = null;
    for (String pair : pairs) {
      String[] kv = pair.split("=");
      String key = kv[0];
      String value = kv[1];
      if ("hash".equals(key)) {
        receivedHash = value;
      } else if ("auth_date".equals(key)) {
        authDateStr = value;
        params.put(key, value);
      } else {
        try {
          String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8);
          params.put(key, decodedValue);
          if ("user".equals(key)) {
            userStr = decodedValue;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    if (!isValidate) {
      return Utils.toObject(userStr, UserTelegramResponse.class);
    }
    if (receivedHash == null) {
      return null;
    }
    if (StringUtils.isBlank(authDateStr)) {
      return null;
    } else if ((Long.parseLong(authDateStr) + timeOut) <= Utils.getCalendar().getTimeInMillis() / 1000) {
      return null;
    }

    StringBuilder dataCheckString = new StringBuilder();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (!dataCheckString.isEmpty()) {
        dataCheckString.append("\n");
      }
      dataCheckString.append(entry.getKey()).append("=").append(entry.getValue());
    }
    try {
      String botTokenData = "WebAppData";
      byte[] hmacSecret = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, botTokenData).hmac(botToken);
      String calculatedHash = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, hmacSecret).hmacHex(
        dataCheckString.toString());

      return calculatedHash.equals(receivedHash) ? Utils.toObject(userStr, UserTelegramResponse.class) : null;
    } catch (Exception e) {
      return null;
    }
  }

  public boolean verifyJoinChannel(String chatId, String userId) {
    try {
      TelegramResponse response = telegramClient.getChatMember(botTokenVerify, chatId, userId);
      if (response.ok && "member".equals(response.result.status) || "administrator".equals(
        response.result.status) || "creator".equals(response.result.status)) {
        return true;
      }
    } catch (Exception e) {
    }
    return false;
  }
}

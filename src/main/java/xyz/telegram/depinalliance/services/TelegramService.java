package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
  public String botToken;

  public UserTelegramResponse validateInitData(String initData) {
    Map<String, String> params = new TreeMap<>();
    String[] pairs = initData.split("&");
    String receivedHash = null;
    String userStr = null;
    for (String pair : pairs) {
      String[] kv = pair.split("=");
      String key = kv[0];
      String value = kv[1];
      if ("hash".equals(key)) {
        receivedHash = value;
      } else {
        try {
          String decodedValue = URLDecoder.decode(value, StandardCharsets.UTF_8.name());
          params.put(key, decodedValue);
          if ("user".equals(key)) {
            userStr = decodedValue;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    if (receivedHash == null) {
      return null;
    }
    StringBuilder dataCheckString = new StringBuilder();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (dataCheckString.length() > 0) {
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
}

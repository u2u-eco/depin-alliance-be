package xyz.telegram.depinalliance.common.configs;

import io.smallrye.config.ConfigMapping;

/**
 * @author holden on 25-Jul-2024
 */
@ConfigMapping(prefix = "twitter")
public interface TwitterConfig {
  String clientId();
  String apiKey();

  String apiSecretKey();

  String callbackUrl();

  String redirectUrl();

  long verifyTime();

  String rapidapiHost();

  String rapidapiKey();

  long rapidapiSleep();
}

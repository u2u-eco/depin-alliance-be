package xyz.telegram.depinalliance.common.configs;

import io.smallrye.config.ConfigMapping;

/**
 * @author holden on 22-Nov-2022
 */
@ConfigMapping(prefix = "amazon")
public interface AmazonS3Config {
  String awsUrl();
}

package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.SystemConfig;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author holden on 26-Aug-2024
 */
@ApplicationScoped
public class SystemConfigService {

  @Inject
  RedissonClient redissonClient;
  @Inject
  Logger logger;

  @ConfigProperty(name = "redis.time-out", defaultValue = "300")
  long timeOut;

  public int getSystemConfigInt(Enums.Config config) {
    return Integer.parseInt(Objects.requireNonNull(findByKey(config)));
  }

  public String findByKey(Enums.Config config) {
    try {
      RBucket<String> value = redissonClient.getBucket(config.name());
      if (value.isExists()) {
        logger.info("Get from cache " + config.name());
        return value.get();
      }
      logger.info("Get from db and set cache " + config.name());
      SystemConfig systemConfig = SystemConfig.findById(config.getType());
      String valueStr = systemConfig != null ? systemConfig.value : null;
      value.setAsync(valueStr, timeOut, TimeUnit.SECONDS);
      return valueStr;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding system config");
    }
    SystemConfig systemConfig = SystemConfig.findById(config.getType());
    String valueStr = systemConfig != null ? systemConfig.value : null;
    return valueStr;
  }
}

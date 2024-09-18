package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.Item;
import xyz.telegram.depinalliance.entities.SystemConfig;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author holden on 26-Aug-2024
 */
@ApplicationScoped
public class RedisService {

  @Inject
  RedissonClient redissonClient;
  @Inject
  Logger logger;

  @ConfigProperty(name = "redis.time-out", defaultValue = "300")
  long timeOut;

  public int getSystemConfigInt(Enums.Config config) {
    return Integer.parseInt(Objects.requireNonNull(findConfigByKey(config)));
  }

  public String findConfigByKey(Enums.Config config) {
    try {
      String redisKey = "CONFIG_" + config.name();
      RBucket<String> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache system config " + config.name());
      SystemConfig systemConfig = SystemConfig.findById(config.getType());
      String valueStr = systemConfig != null ? systemConfig.value : null;
      value.setAsync(valueStr, timeOut, TimeUnit.SECONDS);
      return valueStr;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding system config " + config.name());
    }
    SystemConfig systemConfig = SystemConfig.findById(config.getType());
    String valueStr = systemConfig != null ? systemConfig.value : null;
    return valueStr;
  }

  public Item findItemByCode(String code) {
    try {
      String redisKey = "ITEM_" + code.toUpperCase();
      RBucket<Item> value = redissonClient.getBucket(redisKey);
      if (value.isExists()) {
        return value.get();
      }
      logger.info("Get from db and set cache item " + code.toUpperCase());
      Item item = Item.find("code", code.toUpperCase()).firstResult();
      value.setAsync(item, timeOut, TimeUnit.SECONDS);
      return item;
    } catch (Exception e) {
      logger.errorv(e, "Error while finding item " + code.toUpperCase());
    }
    return Item.find("code", code.toUpperCase()).firstResult();
  }
}

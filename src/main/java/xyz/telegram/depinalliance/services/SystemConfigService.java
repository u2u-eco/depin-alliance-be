package xyz.telegram.depinalliance.services;

import jakarta.enterprise.context.ApplicationScoped;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.SystemConfig;

/**
 * @author holden on 26-Aug-2024
 */
@ApplicationScoped
public class SystemConfigService {
  public int getSystemConfigInt(Enums.Config config) {
    return Integer.parseInt(SystemConfig.findByKey(config));
  }
}

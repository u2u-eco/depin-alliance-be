package xyz.telegram.depinalliance.services;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import xyz.telegram.depinalliance.common.constans.Enums;
import xyz.telegram.depinalliance.entities.DailyCheckin;
import xyz.telegram.depinalliance.entities.Level;
import xyz.telegram.depinalliance.entities.SystemConfig;

import java.math.BigDecimal;

/**
 * @author holden on 26-Aug-2024
 */
public class InitService {

  @Transactional
  void onStart(@Observes StartupEvent event) throws Exception {
    if (LaunchMode.current().isDevOrTest()) {
      SystemConfig systemConfig = SystemConfig.findByKey(Enums.Config.POINT_REF);
      if (systemConfig == null) {
        systemConfig = new SystemConfig();
        systemConfig.id = Enums.Config.POINT_REF.getType();
        systemConfig.value = "200";
        systemConfig.persist();
      }
      if (Level.count() == 0) {
        for (long i = 1; i < 100; i++) {
          Level level = new Level();
          level.id = i;
          level.name = "Level " + i;
          level.exp = new BigDecimal(10 * i);
          level.point = new BigDecimal(100 * i);
          level.persist();
        }
      }
      if (DailyCheckin.count() == 0) {
        for (long i = 1; i < 31; i++) {
          DailyCheckin dailyCheckin = new DailyCheckin();
          dailyCheckin.id = i;
          dailyCheckin.name = "Day " + i;
          dailyCheckin.point = new BigDecimal(10 * i);
          dailyCheckin.persist();
        }
      }
    }
  }
}

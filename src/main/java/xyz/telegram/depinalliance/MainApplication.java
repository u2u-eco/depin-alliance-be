package xyz.telegram.depinalliance;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import xyz.telegram.depinalliance.entities.Level;
import xyz.telegram.depinalliance.entities.SystemConfig;
import xyz.telegram.depinalliance.services.BotService;
import xyz.telegram.depinalliance.services.UserService;

import java.math.BigDecimal;

/**
 * @author holden on 23-Aug-2024
 */
@QuarkusMain
public class MainApplication implements QuarkusApplication {
  @Inject
  UserService userService;

  @Override
  @Transactional
  public int run(String... args) throws Exception {
    String botToken = "7531186714:AAGR_VCfEOgwRJCDcDRWF019yie0nA5LUwc";
    // Using try-with-resources to allow autoclose to run upon finishing
    try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
      botsApplication.registerBot(botToken, new BotService(botToken, "DepinallianceBot", userService));
      System.out.println("Bot successfully started!");
      Thread.currentThread().join();
    } catch (Exception e) {
      e.printStackTrace();
    }
    SystemConfig systemConfig = SystemConfig.find();
    if (systemConfig == null) {
      systemConfig = new SystemConfig();
      systemConfig.pointRef = new BigDecimal(200);
      systemConfig.persist();
    }
    if (Level.count() == 0) {
      for (long i = 1; i < 100; i++) {
        Level level = new Level();
        level.id = i;
        level.name = "Level" + i;
        level.exp = new BigDecimal(10 * i);
        level.point = new BigDecimal(100 * i);
        level.persist();
      }
    }
    return 0;
  }
}

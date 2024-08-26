package xyz.telegram.depinalliance;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import xyz.telegram.depinalliance.services.BotService;
import xyz.telegram.depinalliance.services.UserService;

/**
 * @author holden on 23-Aug-2024
 */
@QuarkusMain
public class MainApplication implements QuarkusApplication {
  @Inject
  UserService userService;
  @Override
  public int run(String... args) throws Exception {
    String botToken = "7531186714:AAGR_VCfEOgwRJCDcDRWF019yie0nA5LUwc";
    // Using try-with-resources to allow autoclose to run upon finishing
    try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
      botsApplication.registerBot(botToken, new BotService(botToken,"DepinallianceBot", userService));
      System.out.println("Bot successfully started!");
      Thread.currentThread().join();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
}

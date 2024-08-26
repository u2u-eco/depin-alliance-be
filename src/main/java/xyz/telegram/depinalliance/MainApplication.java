package xyz.telegram.depinalliance;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import xyz.telegram.depinalliance.services.BotService;
import xyz.telegram.depinalliance.services.UserService;

/**
 * @author holden on 23-Aug-2024
 */
@ApplicationScoped
public class MainApplication {
  @Inject
  UserService userService;
  @ConfigProperty(name = "telegram.token")
  String botToken;
  @ConfigProperty(name = "telegram.run")
  boolean botRun;

  void onStart(@Observes StartupEvent event) {
    if (botRun) {
      new Thread(() -> {
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
          botsApplication.registerBot(botToken, new BotService(botToken, "DepinallianceBot", userService));
          System.out.println("Bot successfully started!");
          Thread.currentThread().join();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }).start();
    }
  }
}

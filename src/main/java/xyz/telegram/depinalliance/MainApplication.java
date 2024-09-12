package xyz.telegram.depinalliance;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
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
  @ConfigProperty(name = "telegram.url")
  String botUrl;
  @ConfigProperty(name = "telegram.bot-username")
  String botUsername;

  void onStart(@Observes StartupEvent event) {
    if (botRun) {
      new Thread(() -> {
        try {
          TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
          botsApi.registerBot(new BotService(botToken, botUrl, botUsername, userService));
          System.out.println("Bot successfully started!");
          Thread.currentThread().join();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }).start();
    }
  }
}

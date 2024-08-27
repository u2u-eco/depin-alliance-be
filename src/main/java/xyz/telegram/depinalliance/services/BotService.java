package xyz.telegram.depinalliance.services;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import xyz.telegram.depinalliance.common.commands.HelpCommand;
import xyz.telegram.depinalliance.common.commands.StartCommand;
import xyz.telegram.depinalliance.common.constans.Emoji;

/**
 * @author holden on 23-Aug-2024
 */

public class BotService extends CommandLongPollingTelegramBot {
  public BotService(String botToken, String botUsername, UserService userService, String url) {
    super(new OkHttpTelegramClient(botToken), true, () -> botUsername);
    register(new StartCommand(userService, botUsername, url));
    HelpCommand helpCommand = new HelpCommand(this);
    register(helpCommand);
    registerDefaultAction((telegramClient, message) -> {
      SendMessage commandUnknownMessage = new SendMessage(String.valueOf(message.getChatId()),
        "The command '" + message.getText() + "' is not known by this bot. Here comes some help " + Emoji.AMBULANCE);
      try {
        telegramClient.execute(commandUnknownMessage);
      } catch (TelegramApiException e) {
      }
      helpCommand.execute(telegramClient, message.getFrom(), message.getChat(), new String[] {});
    });
  }

  @Override
  public void processNonCommandUpdate(Update update) {
    if (update.hasMessage()) {
      Message message = update.getMessage();
      if (message.hasText()) {
        SendMessage echoMessage = new SendMessage(String.valueOf(message.getChatId()),
          "Hey heres your message:\n" + message.getText());
        try {
          telegramClient.execute(echoMessage);
        } catch (TelegramApiException e) {
        }
      }
    }
  }
}

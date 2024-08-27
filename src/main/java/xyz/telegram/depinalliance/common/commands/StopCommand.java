package xyz.telegram.depinalliance.common.commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class StopCommand extends BotCommand {

  public StopCommand() {
    super("stop", "With this command you can stop the Bot");
  }

  @Override
  public void execute(TelegramClient telegramClient, User user, Chat chat, String[] arguments) {

    String userName = user.getFirstName() + " " + user.getLastName();

    SendMessage answer = new SendMessage(chat.getId().toString(),
      "Good bye " + userName + "\n" + "Hope to see you soon!");

    try {
      telegramClient.execute(answer);
    } catch (TelegramApiException e) {
    }
  }
}

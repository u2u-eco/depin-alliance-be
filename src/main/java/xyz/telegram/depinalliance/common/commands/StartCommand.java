package xyz.telegram.depinalliance.common.commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import xyz.telegram.depinalliance.services.UserService;

/**
 * This commands starts the conversation with the bot
 *
 * @author Timo Schulz (Mit0x2)
 */
public class StartCommand extends BotCommand {
  public StartCommand(UserService userService) {
    super("start", "With this command you can start the Bot");
    this.userService = userService;
  }

  UserService userService;

  @Override
  public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
    StringBuilder messageBuilder = new StringBuilder();
    String userName = user.getUserName();
    String refCode = "";
    if (strings != null && strings.length > 0) {
      refCode = strings[0];
    }
    synchronized (chat.getId().toString().intern()) {
      userService.checkStartUser(chat.getId(), user.getUserName(), refCode);
    }
    messageBuilder.append("Welcome ").append(userName).append("\n");
    messageBuilder.append("this bot will demonstrate you the command feature of the Java TelegramBots API!");
    SendMessage answer = new SendMessage(chat.getId().toString(), messageBuilder.toString());
    try {
      telegramClient.execute(answer);
    } catch (TelegramApiException e) {
    }
  }
}
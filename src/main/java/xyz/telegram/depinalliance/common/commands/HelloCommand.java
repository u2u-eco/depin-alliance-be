package xyz.telegram.depinalliance.common.commands;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class HelloCommand extends BotCommand {

  public HelloCommand() {
    super("hello", "Say hallo to this bot");
  }

//  public void execute(TelegramClient telegramClient, User user, Chat chat, String[] arguments) {
//
//    String userName = chat.getUserName();
//    if (userName == null || userName.isEmpty()) {
//      userName = user.getFirstName() + " " + user.getLastName();
//    }
//
//    StringBuilder messageTextBuilder = new StringBuilder("Hello ").append(userName);
//    if (arguments != null && arguments.length > 0) {
//      messageTextBuilder.append("\n");
//      messageTextBuilder.append("Thank you so much for your kind words:\n");
//      messageTextBuilder.append(String.join(" ", arguments));
//    }
//
//    SendMessage answer = new SendMessage(chat.getId().toString(), messageTextBuilder.toString());
//
//    try {
//      telegramClient.execute(answer);
//    } catch (TelegramApiException e) {
//    }
//  }

  @Override
  public void execute(TelegramClient telegramClient, User user,
    org.telegram.telegrambots.meta.api.objects.chat.Chat chat, String[] arguments) {
    String userName = chat.getUserName();
    if (userName == null || userName.isEmpty()) {
      userName = user.getFirstName() + " " + user.getLastName();
    }
    StringBuilder messageTextBuilder = new StringBuilder("Hello ").append(userName);
    if (arguments != null && arguments.length > 0) {
      messageTextBuilder.append("\n");
      messageTextBuilder.append("Thank you so much for your kind words:\n");
      messageTextBuilder.append(String.join(" ", arguments));
    }
    SendMessage answer = new SendMessage(chat.getId().toString(), messageTextBuilder.toString());
    try {
      telegramClient.execute(answer);
    } catch (TelegramApiException e) {
    }

  }
}
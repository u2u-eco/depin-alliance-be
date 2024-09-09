package xyz.telegram.depinalliance.common.commands;

import io.quarkus.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import xyz.telegram.depinalliance.services.UserService;

import java.util.Arrays;

public class StartCommand extends BotCommand {
  private final String url;
  private final String botName;
  private final InlineKeyboardMarkup inlineKeyboardMarkup;
  private UserService userService;

  public StartCommand(UserService userService, String botName, String url) {
    super("start", "With this command you can start the Bot");
    this.userService = userService;
    this.url = url;
    this.botName = botName;
    this.inlineKeyboardMarkup = new InlineKeyboardMarkup(
      Arrays.asList(new InlineKeyboardRow(InlineKeyboardButton.builder().text("\uD83D\uDD79️ Play").webApp(new WebAppInfo(url)).build()),
        new InlineKeyboardRow(InlineKeyboardButton.builder().text("Join Community").url("https://t.me/DePIN_App").build()),
        new InlineKeyboardRow(InlineKeyboardButton.builder().text("Follow X").url("https://x.com/DePINApp").build())));
  }

  @Override
  public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
    try {
      StringBuilder messageBuilder = new StringBuilder();
      String refCode = "";
      if (strings != null && strings.length > 0) {
        refCode = strings[0];
      }
      String league = "";
      if (StringUtils.isNotBlank(refCode) && refCode.contains("_")) {
        String[] arrays = refCode.split("_");
        refCode = arrays[0];
        league = arrays[1];
      }
      String username = StringUtils.isBlank(user.getUserName()) ?
        (StringUtils.isBlank(user.getFirstName()) ?
          (StringUtils.isBlank(user.getLastName()) ? chat.getId().toString() : user.getLastName()) :
          user.getFirstName()) :
        user.getUserName();
      synchronized (chat.getId().toString().intern()) {
        userService.checkStartUser(chat.getId(), username, refCode, league, user.getIsPremium());
      }
      messageBuilder.append("<b>Welcome to DePIN Alliance!</b> \uD83D\uDEA9\n\n");
      messageBuilder.append(
        "The Odyssey of Decentralists for those who dare to challenge the current state and embrace the future of decentralized innovation.\n\n");
      messageBuilder.append("\uD83D\uDE80 <b>How to Get Started:</b>\n\n");
      messageBuilder.append("1️⃣ Access the telegram bot: https://t.me/").append(botName).append("\n");
      messageBuilder.append("2️⃣ Click \"Get Started\" to verify your device.\n");
      messageBuilder.append("3️⃣ Start mining to earn additional points.\n");
      messageBuilder.append("4️⃣ Explore more Challenges and Rewards.\n");
      messageBuilder.append("5️⃣ Invite friends to join this adventure together.\n\n");
      messageBuilder.append(
        "\uD83D\uDCCC The device verification process will not affect your device and all data is securely stored locally on your device.\n\n");
      messageBuilder.append("\uD83D\uDC49 Click the play and start your journey with DePIN Alliance today!");
      SendMessage answer = new SendMessage(chat.getId().toString(), messageBuilder.toString());
      answer.setReplyMarkup(inlineKeyboardMarkup);
      answer.enableHtml(true);
      telegramClient.execute(answer);
    } catch (TelegramApiException e) {
      Log.error("User " + user.getUserName() + "has error", e);
    }
  }
}
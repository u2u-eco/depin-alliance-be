package xyz.telegram.depinalliance.services;

import io.quarkus.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author holden on 23-Aug-2024
 */

public class BotService extends TelegramLongPollingBot {

  private final String botName;
  private final InlineKeyboardMarkup inlineKeyboardMarkup;
  private final String messageStr;
  private final UserService userService;
  Executor updatesProcessorExecutor = Executors.newFixedThreadPool(20);

  public BotService(String botToken, String url, String botName, UserService userService) {
    super(botToken);
    this.botName = botName;
    this.inlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboardRow(Collections.singletonList(
        InlineKeyboardButton.builder().text("\uD83D\uDD79️ Play").webApp(new WebAppInfo(url)).build())).keyboardRow(
        Collections.singletonList(
          InlineKeyboardButton.builder().text("Join Community").url("https://t.me/DePIN_App").build())).keyboardRow(
        Collections.singletonList(InlineKeyboardButton.builder().text("Follow X").url("https://x.com/DePINApp").build()))
      .build();
    String messageBuilder = "<b>Welcome to DePIN Alliance!</b> \uD83D\uDEA9\n\n" + "The Odyssey of Decentralists for those who dare to challenge the current state and embrace the future of decentralized innovation.\n\n" + "\uD83D\uDE80 <b>How to Get Started:</b>\n\n" + "1️⃣ Access the telegram bot: https://t.me/" + botName + "\n" + "2️⃣ Click \"Get Started\" to verify your device.\n" + "3️⃣ Start mining to earn additional points.\n" + "4️⃣ Explore more Challenges and Rewards.\n" + "5️⃣ Invite friends to join this adventure together.\n\n" + "\uD83D\uDCCC The device verification process will not affect your device and all data is securely stored locally on your device.\n\n" + "\uD83D\uDC49 Click the play and start your journey with DePIN Alliance today!";
    this.messageStr = messageBuilder;
    this.userService = userService;
  }

  @Override
  public void onUpdateReceived(Update update) {
    updatesProcessorExecutor.execute(() -> {
      try {
        if (update.hasMessage()) {
          Message message = update.getMessage();
          User user = message.getFrom();
          String username = StringUtils.isBlank(user.getUserName()) ?
            (StringUtils.isBlank(user.getFirstName()) ?
              (StringUtils.isBlank(user.getLastName()) ? message.getChat().getId().toString() : user.getLastName()) :
              user.getFirstName()) :
            user.getUserName();
          Log.info("Received from " + username + " message " + message.getText());
          if (message.isCommand()) {
            String text = message.getText();
            if (text.startsWith("/start") && message.isUserMessage()) {
              try {
                String refCode = "";
                String[] strings = StringUtils.split(text, " ");
                if (strings != null && strings.length > 1) {
                  refCode = strings[1];
                }
                String league = "";
                if (StringUtils.isNotBlank(refCode) && refCode.contains("_")) {
                  String[] arrays = refCode.split("_");
                  refCode = arrays[0];
                  league = arrays[1];
                }
                Long chatId = message.getChatId();
                synchronized (chatId.toString().intern()) {
                  userService.checkStartUser(chatId, username, refCode, league, user.getIsPremium());
                }
                SendMessage answer = new SendMessage(message.getChat().getId().toString(), messageStr);
                answer.setReplyMarkup(inlineKeyboardMarkup);
                answer.enableHtml(true);
                executeAsync(answer);
              } catch (TelegramApiException e) {
                Log.error("User " + user + "has error", e);
              }
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public String getBotUsername() {
    return botName;
  }
}

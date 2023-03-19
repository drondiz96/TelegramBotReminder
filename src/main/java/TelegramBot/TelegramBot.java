package TelegramBot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.text.ParseException;


import java.text.SimpleDateFormat;
import java.util.*;


import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.util.stream.Stream;

@Slf4j
@Getter
@SpringBootApplication
public class TelegramBot extends TelegramLongPollingBot{
    public static void main(String[] args) {
        TelegramBot bot = new TelegramBot();

        TelegramBotsApi telegramBotsApi = null;

        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {

        }
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {

        }
    }

    public String getBotUsername() {

        return "My_first_bot2023_bot";
    }
    @Override
    public String getBotToken() {

        return "6149005637:AAHmkNcZrNCjR1ZmuRXyzrVvm7G_wcrcmPw";
    }
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage())
        {
            if (update.getMessage().hasText()) {

                String answer = update.getMessage().getText();
                if(answer.equals("/start"))
                {
                    try {
                        execute(SendMessage
                                .builder()
                                .chatId(update.getMessage().getChatId().toString())
                                .text("Добро пожаловать в телеграмм бот!")
                                .build());

                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if(condition.write_text){
                    try {
                        IdLastMessage = execute(SendMessage
                                .builder()
                                .chatId(update.getMessage().getChatId().toString())
                                .text("Завершить настройку?")
                                .replyMarkup(sendInlineKeyBoardMessage())
                                .build()).getMessageId();
                        condition.write_text = false;
                        SetTextReminder(answer);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if(condition.write_date){
                    try {
                        IdLastMessage = execute(SendMessage
                                .builder()
                                .chatId(update.getMessage().getChatId().toString())
                                .replyMarkup(cansel())
                                .text("Пожалуйста, введите текст.")
                                .build()).getMessageId();

                        SetDateReminder(answer);
                        condition.write_text = true;
                        condition.write_date = false;
                    } catch (ParseException e) {
                        try {
                            IdLastMessage = execute(SendMessage
                                    .builder()
                                    .chatId(update.getMessage().getChatId().toString())
                                    .replyMarkup(cansel())
                                    .text("Пожалуйста, введите Время в формате \"dd.MM.yyyy HH.mm\" (пример 31.12.2023 22.30)")
                                    .build()).getMessageId();
                            condition.write_date = true;
                            condition.write_text = false;
                        } catch (TelegramApiException E) {
                            throw new RuntimeException(E);
                        }
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if(answer.equals("/setrem")){
                    try {
                        IdLastMessage = execute(SendMessage
                                .builder()
                                .chatId(update.getMessage().getChatId().toString())
                                .replyMarkup(cansel())
                                .text("Пожалуйста, введите Время в формате \"dd.MM.yyyy HH.mm\" (пример 31.12.2023 22.30)")
                                .build()).getMessageId();
                        condition.write_date = true;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    try {
                        execute(SendMessage
                                .builder()
                                .chatId(update.getMessage().getChatId().toString())
                                .text(answer)
                                .build());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            if(update.getCallbackQuery().getData().toString().equals("Настройка закончена.")) {
                DeleteMessage deleteMessage = new DeleteMessage(update.getCallbackQuery().getMessage().getChatId().toString(), IdLastMessage);
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                EndSetting(true ,update.getCallbackQuery().getMessage().getChatId().toString());
                try {
                    execute(SendMessage
                            .builder()
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .text("Настройка напоминалки закончена.")
                            .build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            else
            if(update.getCallbackQuery().getData().toString().equals("Отмена")) {
                DeleteMessage deleteMessage = new DeleteMessage(update.getCallbackQuery().getMessage().getChatId().toString(), IdLastMessage);
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                EndSetting(false ,update.getCallbackQuery().getMessage().getChatId().toString());
                try {
                    execute(SendMessage
                            .builder()
                            .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                            .text("Настройка напоминалки отменена.")
                            .build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static InlineKeyboardMarkup sendInlineKeyBoardMessage() {
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Конец настройки.");
        inlineKeyboardButton1.setCallbackData("Настройка закончена.");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("Отмена");
        inlineKeyboardButton2.setCallbackData("Отмена");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        return  new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup cansel() {
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Отмена");
        inlineKeyboardButton1.setCallbackData("Отмена");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        return  new InlineKeyboardMarkup(rowList);
    }
    Integer IdLastMessage;
    Condition condition = new Condition();
    static class Condition{
        Boolean write_date;
        Boolean write_text;
        Condition() {
            write_date = false;
            write_text = false;
        }
    }
    Reminder reminder = new Reminder();
    Stream<Reminder> reminderStream = Stream.empty();
    static class Reminder {
        Data data;
        Timer timer;
        Reminder() {
            data = new Data();
            timer = new Timer();
        }
        Reminder(Data data, Timer timer) {
            this.data = data;
            this.timer = timer;
        }
        void setText(String text) { data.setText(text); }
        String getText() { return data.getText(); }
        void setChatId(String chatId) { data.setChatId(chatId); }
        String getChatId()
        { return data.getChatId(); }
        void setDate(Date date) { data.setDate(date); }
        Date getDate() { return data.getDate(); }
    }

    static class Data {
        @Setter
        @Getter
        String chatId;
        @Setter
        @Getter
        String text;
        @Setter
        @Getter
        Date date;
        public Data(){}
        public Data(String chatId, String text)
        {
            this.chatId = chatId;
            this.text = text;
        }
    }
    public void SetTextReminder(String text) {
        reminder.setText(text);
    }
    public void SetDateReminder(String time) throws ParseException {
        reminder.setDate(new SimpleDateFormat("dd.MM.yyyy HH.mm").parse(time));
    }
    public void EndSetting(Boolean bool,String chatId) {
        if(bool)
        {
            reminder.setChatId(chatId);
            Timer timer = new Timer();
            Reminder tmp = new Reminder(new Data(reminder.getChatId(), reminder.getText()), timer);
            reminderStream = Stream.concat(reminderStream, Stream.of(tmp));
            timer.schedule(new Scheduling(), reminder.getDate());
        }
        condition.write_date = false;
        condition.write_text = false;
    }

    private class Scheduling extends TimerTask {
        Data reminder_one = new Data(reminder.getChatId(), reminder.getText());
        public void run() {
            try {
                execute(SendMessage
                        .builder()
                        .chatId(reminder_one.getChatId())
                        .text(reminder_one.getText())
                        .build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

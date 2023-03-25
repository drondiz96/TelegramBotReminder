package TelegramBot.bot;

import lombok.Getter;
import TelegramBot.services.ReminderData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;


import java.util.*;


import java.util.Timer;
import java.util.TimerTask;


public class TelegramBot extends TelegramLongPollingBot{
    ReminderData reminder = new ReminderData();
    @Override
    public String getBotUsername() { return "${bot.name}"; }
    @Override
    public String getBotToken() { return "6149005637:AAHmkNcZrNCjR1ZmuRXyzrVvm7G_wcrcmPw"; }
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage())
        {
            if (!update.getMessage().hasText())
                return;
            String answer = update.getMessage().getText();
            String ChatId = update.getMessage().getChatId().toString();
            if(Processing(ChatId, answer))
                return;
            if(answer.equals("/start"))
                StartCommand(ChatId);
            else if(answer.equals("/setrem")){
                printDateFormat(ChatId);
                condition.write_date = true;
            }
            else
                SndMsg(ChatId, answer);
        }
        if (!update.hasCallbackQuery())
            return;
        String ChatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String answer = update.getCallbackQuery().getData().toString();
        DeleteMsg(ChatId);
        if(answer.equals("Настройка закончена")) {
            EndSetting_StartReminder(true ,ChatId);
            SndMsg(ChatId, "Настройка напоминалки закончена.");
        }
        else if(answer.equals("Отмена")) {
            EndSetting_StartReminder(false, ChatId);
            SndMsg(ChatId, "Настройка напоминалки отменена.");
        }

    }
    public static InlineKeyboardMarkup EndSettingKbd() {
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Конец настройки");
        inlineKeyboardButton1.setCallbackData("Настройка закончена");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("Отмена");
        inlineKeyboardButton2.setCallbackData("Отмена");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        return  new InlineKeyboardMarkup(rowList);
    }
    public static InlineKeyboardMarkup Cansel() {
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Отмена");
        inlineKeyboardButton1.setCallbackData("Отмена");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        return new InlineKeyboardMarkup(rowList);
    }
    void EndSetting(String ChatId){
        SndMsgWithKbd(ChatId, "Завершить настройку?", EndSettingKbd());
        condition.write_date = false;
        condition.write_text = false;
    }
    void StartCommand(String ChatId) {
        SndMsg("Добро пожаловать в телеграмм бот!\n↓ меню", ChatId);
    }
    void printDateFormat(String ChatId) {
        SndMsgWithKbd(ChatId, "Пожалуйста, введите время в формате \"dd.MM.yyyy HH:mm\" (пример 01.01.2023 00:00)", Cansel());
    }
    void printTextFormat(String ChatId){
        SndMsgWithKbd(ChatId, "Пожалуйста, введите текст.", Cansel());
    }
    void SndMsg(String ChatId, String text) {
        try {
            execute(SendMessage
                    .builder()
                    .text(text)
                    .chatId(ChatId)
                    .build());

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    void SndMsgWithKbd(String ChatId, String text, InlineKeyboardMarkup Keyboard) {
        try {
            IdLastMessage = execute(SendMessage
                    .builder()
                    .chatId(ChatId)
                    .replyMarkup(Keyboard)
                    .text(text)
                    .build()).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    void DeleteMsg(String ChatId)
    {
        try {
            execute(new DeleteMessage(ChatId, IdLastMessage));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    boolean Processing(String ChatId, String text){
        if(condition.write_text){
            EndSetting(ChatId);
            condition.write_text = false;
            reminder.SetTextReminder(text);
        }
        else if(condition.write_date){
            try {
                DeleteMsg(ChatId);
                reminder.SetDateReminder(text);

                printTextFormat(ChatId);
                condition.write_text = true;
                condition.write_date = false;
            } catch (ParseException e) {
                printDateFormat(ChatId);
                condition.write_text = false;
                condition.write_date = true;
            }
        }
        else return false;
        return true;
    }
    Integer IdLastMessage;
    Condition condition = new Condition();
    static class Condition {
        Boolean write_date;
        Boolean write_text;
        Condition() {
            write_date = false;
            write_text = false;
        }
    }
    public void EndSetting_StartReminder(boolean StartReminder,String chatId) {
        if(!StartReminder)
            return;
        reminder.setChatId(chatId);
        Timer timer = new Timer();
        timer.schedule(new Scheduling(), reminder.getDate());
    }
    private class Scheduling extends TimerTask {
        ReminderData.Data reminder_one = new ReminderData.Data(reminder.getChatId(), reminder.getText(), reminder.getDate());
        public void run() {
            SndMsg(reminder_one.getChatId(), reminder_one.getText());
        }
    }
}

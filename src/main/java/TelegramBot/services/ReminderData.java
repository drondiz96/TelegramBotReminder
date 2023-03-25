package TelegramBot.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderData {
    static Reminder reminder = new Reminder();
    public String getText() { return reminder.getText(); }
    public void setChatId(String chatId) { reminder.setChatId(chatId); }
    public String getChatId() { return reminder.getChatId(); }
    public Date getDate() { return reminder.getDate(); }

    @NoArgsConstructor
    @AllArgsConstructor
    static class Reminder {
        private Data data;
        void setText(String text) { data.setText(text); }
        String getText() { return data.getText(); }
        void setChatId(String chatId) { data.setChatId(chatId); }
        String getChatId() { return data.getChatId(); }
        void setDate(Date date) { data.setDate(date); }
        Date getDate() { return data.getDate(); }
    }
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static public class Data {
        private String chatId;
        private String text;
        private Date date;
    }
    public void SetTextReminder(String text) {
        reminder.setText(text);
    }
    public void SetDateReminder(String time) throws ParseException {
        reminder.setDate(new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(time));
    }
}

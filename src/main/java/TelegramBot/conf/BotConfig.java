package TelegramBot.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
//-------------------------------пока не используется--------------------------------------//
@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {
    @Value("${bot.name}") String botName;
    @Value("${bot.token}") String token;
}
//-------------------------------пока не используется--------------------------------------//
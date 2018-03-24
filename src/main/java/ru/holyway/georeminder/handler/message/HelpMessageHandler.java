package ru.holyway.georeminder.handler.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Component
public class HelpMessageHandler implements MessageHandler {
    @Override
    public boolean isNeedToHandle(Message message) {
        final String mes = message.getText();
        return StringUtils.isNotEmpty(mes) && (mes.contains("/start") || mes.contains("/help"));
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Привет! Я GEO Note, но друзья называют меня просто Гео.\n")
                .append("Я могу помочь тебе не забыть сделать важное дело, когда ты будешь находиться в определенном месте.\n")
                .append("Для этого, тебе необходимо создать задачу, используя команды /new или 'новая'\n")
                .append("После чего указать адрес места, рядом с которым необходимо вывести напоминание\n")
                .append("Ты можешь задать место разными способами: ввести адрес вручную, указать точку на карте или вовсе" +
                        " определив ключевое слово, например название магазина или категория объекта\n")
                .append("После чего ты должен включить фуникцию \"поделиться своим местоположеним\" с ботом, на 1 или 8 часов\n")
                .append("И когда ты будещь рядом с необходимым местом, я сообщу тебе\n\n")
                .append("Так же, ты можешь обращаться ко мне в групповых чатах через команды или добавляю 'Гео,' перед сообщением, и делиться задачми с друзьями\n")
                .append("В Групповых чатах ты также можешь создать эвент, используя команду /event, и задать адрес проведения эвента. \n")
                .append("После чего, все друзья, включившие шеринг локации, будут информироваться о приближении к друг другу а так же достижении места эвента")
                .append("Список активных задач можно посмотреть командой /list или просто введя слово 'список'");

        sender.execute(new SendMessage().setText(stringBuilder.toString()).setChatId(message.getChatId()));
    }
}

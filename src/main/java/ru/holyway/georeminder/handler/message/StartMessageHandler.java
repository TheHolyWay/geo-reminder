package ru.holyway.georeminder.handler.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

@Component
public class StartMessageHandler implements MessageHandler {

    private final UserStateService userStateService;

    public StartMessageHandler(UserStateService userStateService) {
        this.userStateService = userStateService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        if (UserState.NO_STATE.equals(userStateService.getCurrentUserState(message.getFrom().getId()))) {
            final String mes = message.getText();
            return StringUtils.isNotEmpty(mes) && "/new".equalsIgnoreCase(mes);
        }
        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        sender.execute(new SendMessage().setText("\uD83D\uDEE3 Пришлите необходимую локацию или напишите адрес").setChatId(message.getChatId()));
        userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_LOCATION);
    }
}

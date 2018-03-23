package ru.holyway.georeminder.handler.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

@Component
public class TypeCallbackHandler implements CallbackHandler {
    private final UserStateService userStateService;

    public TypeCallbackHandler(UserStateService userStateService) {
        this.userStateService = userStateService;
    }

    @Override
    public boolean isNeedToHandle(CallbackQuery callbackQuery) {
        return userStateService.getCurrentUserState(callbackQuery.getFrom().getId()).equals(UserState.ASK_TYPE);
    }

    @Override
    public void execute(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {
        final String callbackData = callbackQuery.getData();
        if (callbackData.equals("address")) {
            sender.execute(new SendMessage().setText("\uD83D\uDEE3 Пришлите необходимую локацию или напишите адрес").setChatId(callbackQuery.getMessage().getChatId()));
            userStateService.changeUserState(callbackQuery.getFrom().getId(), UserState.ASK_LOCATION);
        }
        if (callbackData.equals("place")) {
            sender.execute(new SendMessage().setText("\uD83D\uDEE3 Укажите место").setChatId(callbackQuery.getMessage().getChatId()));
            userStateService.changeUserState(callbackQuery.getFrom().getId(), UserState.ASK_PLACE);
        }
    }
}

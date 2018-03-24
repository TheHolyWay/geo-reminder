package ru.holyway.georeminder.handler.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

import java.util.ArrayList;
import java.util.List;

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
            return StringUtils.isNotEmpty(mes) && (mes.contains("/new") || StringUtils.containsIgnoreCase(mes, "нов"));
        }
        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton addressButton = new InlineKeyboardButton("Address");
        addressButton.setText("\uD83D\uDC41\u200D\uD83D\uDDE8  Адрес");
        addressButton.setCallbackData("address");
        buttons.add(addressButton);
        InlineKeyboardButton placeButton = new InlineKeyboardButton("Place");
        placeButton.setCallbackData("place");
        placeButton.setText("\uD83D\uDD0E  Поиск места");
        buttons.add(placeButton);
        buttonList.add(buttons);
        inlineKeyboardMarkup.setKeyboard(buttonList);
        sender.execute(new SendMessage().setText("\uD83D\uDEE3  Выберете, пожалуйста, как Вы хотите указать локацию:\n\n" +
                "\uD83D\uDC41\u200D\uD83D\uDDE8  Адрес - можно указать конкретный адрес или точку на карте\n" +
                "\uD83D\uDD0E  Поиск места - можно указать название места или поисковое слово (например 'Аптека' или 'Магнит')")
                .setReplyMarkup(inlineKeyboardMarkup)
                .setChatId(message.getChatId()));
        userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_TYPE);
        final UserTask userTask = new UserTask();
        userTask.setEventType(UserTask.EventType.SINGLE);
        userStateService.changeDraftTask(message.getFrom().getId(), userTask);
    }
}

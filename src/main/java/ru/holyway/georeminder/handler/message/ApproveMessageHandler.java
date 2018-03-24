package ru.holyway.georeminder.handler.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

@Component
public class ApproveMessageHandler implements MessageHandler {

    private final UserStateService userStateService;

    public ApproveMessageHandler(UserStateService userStateService) {
        this.userStateService = userStateService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        if (UserState.ASK_APPROVE_ADDRESS.equals(userStateService.getCurrentUserState(message.getFrom().getId()))) {
            final String mes = message.getText();
            return StringUtils.isNotEmpty(mes) && ("+".equals(mes) || "да".equalsIgnoreCase(mes) || "ага".equalsIgnoreCase(mes) || "-".equals(mes) || "нет".equalsIgnoreCase(mes));
        }
        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        final String mes = message.getText();
        if ("+".equals(mes.trim()) || "да".equalsIgnoreCase(mes.trim()) || "ага".equalsIgnoreCase(mes.trim()) || "угу".equalsIgnoreCase(mes.trim())) {
            final UserTask userTask = userStateService.getDraftUserTask(message.getFrom().getId());
            if (UserTask.EventType.EVENT == userTask.getEventType()) {
                sender.execute(new SendMessage().setText("\uD83D\uDCDD Напишите название эвента").setChatId(message.getChatId()));
            } else {
                sender.execute(new SendMessage().setText("\uD83D\uDCAC Напишите текст напоминания").setChatId(message.getChatId()));
            }
            userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_MESSAGE);
        } else if ("-".equals(mes) || "нет".equalsIgnoreCase(mes)) {
            sender.execute(new SendMessage().setText("\uD83D\uDEE3 Попробуйте снова прислать необходимую локацию или напишите адрес").setChatId(message.getChatId()));
            userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_LOCATION);
        }
    }
}

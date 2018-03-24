package ru.holyway.georeminder.handler.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.UserTaskService;

import java.util.Set;

@Component
public class CancelEventCallbackHandler implements CallbackHandler {

    private final UserTaskService userTaskService;

    public CancelEventCallbackHandler(final UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }

    @Override
    public boolean isNeedToHandle(CallbackQuery callbackQuery) {
        final String callbackData = callbackQuery.getData();
        return callbackData.startsWith("event_cancel:");
    }

    @Override
    public void execute(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {
        final String callbackData = callbackQuery.getData();
        if (!callbackQuery.getMessage().getChat().isUserChat()) {
            final Long userID = callbackQuery.getMessage().getChatId();
            executeCancelDelay(callbackQuery, sender, callbackData, userID);
        }
    }

    private void executeCancelDelay(CallbackQuery callbackQuery, AbsSender sender, String callbackData, Number userID) throws TelegramApiException {
        final String id = callbackData.replace("event_cancel:", "");
        Set<UserTask> userTaskList = userTaskService.getUserTasks(userID);
        for (UserTask userTask : userTaskList) {
            if (userTask.getId().equals(id)) {
                userTaskService.removeUserTask(id);
                sender.execute(new SendMessage().setText("✔ Эвент завершен")
                        .setReplyToMessageId(callbackQuery.getMessage().getMessageId())
                        .setChatId(callbackQuery.getMessage().getChatId()));
                sender.execute(new EditMessageReplyMarkup().setChatId(callbackQuery.getMessage().getChatId()).setMessageId(callbackQuery.getMessage().getMessageId()));
                return;
            }
        }
    }
}

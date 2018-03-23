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
import java.util.concurrent.TimeUnit;

@Component
public class DefaultCallbackHandler implements CallbackHandler {

    private final UserTaskService userTaskService;

    public DefaultCallbackHandler(final UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }

    @Override
    public boolean isNeedToHandle(CallbackQuery callbackQuery) {
        final String callbackData = callbackQuery.getData();
        return callbackData.startsWith("delay:") || callbackData.startsWith("cancel:");
    }

    @Override
    public void execute(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {
        final String callbackData = callbackQuery.getData();
        if (callbackData.startsWith("delay:")) {
            final String id = callbackData.replace("delay:", "");
            Set<UserTask> userTaskList = userTaskService.getUserTasks(callbackQuery.getFrom().getId());
            for (UserTask userTask : userTaskList) {
                if (userTask.getId().equals(id)) {
                    userTask.setNotifyTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));
                    userTaskService.updateTask(userTask);
                    sender.execute(new SendMessage().setText("\uD83D\uDD53 Ок, помолчу про неё пол часа").setChatId(callbackQuery.getMessage().getChatId()));
                    sender.execute(new EditMessageReplyMarkup().setChatId(callbackQuery.getMessage().getChatId()).setMessageId(callbackQuery.getMessage().getMessageId()));
                    return;
                }
            }
        }
        if (callbackData.startsWith("cancel:")) {
            final String id = callbackData.replace("cancel:", "");
            Set<UserTask> userTaskList = userTaskService.getUserTasks(callbackQuery.getFrom().getId());
            for (UserTask userTask : userTaskList) {
                if (userTask.getId().equals(id)) {
                    userTask.setNotifyTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30));
                    userTaskService.removeUserTask(id);
                    sender.execute(new SendMessage().setText("✔ Задача завершена ").setChatId(callbackQuery.getMessage().getChatId()));
                    sender.execute(new EditMessageReplyMarkup().setChatId(callbackQuery.getMessage().getChatId()).setMessageId(callbackQuery.getMessage().getMessageId()));
                    return;
                }
            }
        }
    }
}

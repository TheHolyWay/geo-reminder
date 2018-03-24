package ru.holyway.georeminder.handler.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.PlaceTaskService;
import ru.holyway.georeminder.service.UserTaskService;

import java.util.Set;

@Component
public class DeleteTaskCallbackHandler implements CallbackHandler {

    private final UserTaskService userTaskService;
    private final PlaceTaskService placeTaskService;

    public DeleteTaskCallbackHandler(UserTaskService userTaskService,
                                     PlaceTaskService placeTaskService) {
        this.userTaskService = userTaskService;
        this.placeTaskService = placeTaskService;
    }

    @Override
    public boolean isNeedToHandle(CallbackQuery callbackQuery) {
        final String callbackData = callbackQuery.getData();
        return callbackData.startsWith("delete_task:");
    }

    @Override
    public void execute(CallbackQuery callbackQuery, AbsSender sender) throws TelegramApiException {
        final String taskId = callbackQuery.getData().replace("delete_task:", "");
        final Set<UserTask> userTasks = userTaskService.getUserTasks(callbackQuery.getFrom().getId());
        UserTask requestedTask = null;

        for (UserTask task : userTasks) {
            if (taskId.equals(task.getId())) {
                requestedTask = task;
                break;
            }
        }

        if (UserTask.TaskType.PLACE == requestedTask.getTaskType()) {
            placeTaskService.removePlaceByTask(requestedTask.getId());
        }

        userTaskService.removeUserTask(taskId);

        sender.execute(new DeleteMessage().setMessageId(callbackQuery.getMessage().getMessageId())
                .setChatId(callbackQuery.getMessage().getChatId().toString()));
        sender.execute(new AnswerCallbackQuery()
                .setText("❌ Удалено")
                .setShowAlert(true)
                .setCallbackQueryId(callbackQuery.getId()));
    }
}

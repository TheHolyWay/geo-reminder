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
import ru.holyway.georeminder.service.UserTaskService;

@Component
public class TaskTextMessageHandler implements MessageHandler {

    private final UserStateService userStateService;

    private final UserTaskService userTaskService;

    public TaskTextMessageHandler(UserStateService userStateService, UserTaskService userTaskService) {
        this.userStateService = userStateService;
        this.userTaskService = userTaskService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        if (UserState.ASK_MESSAGE.equals(userStateService.getCurrentUserState(message.getFrom().getId()))) {
            final String mes = message.getText();
            return StringUtils.isNotEmpty(mes);
        }
        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        UserTask userTask = userStateService.getDraftUserTask(message.getFrom().getId());
        final String mes = message.getText();
        userTask.setMessage(mes);
        final StringBuilder taskTextMessage = new StringBuilder("✅  Задача создана\n");
        taskTextMessage.append("\uD83D\uDEE3  Место: ");
        if (userTask.getTaskType().equals(UserTask.TaskType.SIMPLE)) {
            taskTextMessage.append(userTask.getTargetPlace());
        } else {
            taskTextMessage.append(userTask.getTargetPlace()).append(" по близости");
        }
        taskTextMessage.append("\n\uD83D\uDCDD  Текст напоминания: ").append(userTask.getMessage());

        sender.execute(new SendMessage().setText(taskTextMessage.toString()).setChatId(message.getChatId()));
        userTaskService.addTask(userTask);
        userStateService.changeUserState(message.getFrom().getId(), UserState.NO_STATE);
    }
}

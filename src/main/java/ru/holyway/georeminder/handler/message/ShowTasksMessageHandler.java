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
import ru.holyway.georeminder.service.UserTaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ShowTasksMessageHandler implements MessageHandler {

    private final UserStateService userStateService;
    private final UserTaskService userTaskService;

    public ShowTasksMessageHandler(UserStateService userStateService,
                                   UserTaskService userTaskService) {
        this.userStateService = userStateService;
        this.userTaskService = userTaskService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        if (UserState.NO_STATE.equals(userStateService.getCurrentUserState(message.getFrom().getId()))) {
            final String mes = message.getText();

            return StringUtils.isNotEmpty(mes) && (mes.contains("/list")
                    || StringUtils.containsIgnoreCase(mes, "список")
                    || StringUtils.containsIgnoreCase(mes, "мои")
                    || StringUtils.containsIgnoreCase(mes, "таски"));
        }

        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {

        Set<UserTask> userTasks;
        if (message.getChat().isUserChat()) {
            userTasks = userTaskService.getUserTasks(message.getFrom().getId());
            sender.execute(new SendMessage().setText(message.getFrom().getFirstName() + ", твои активные таски:")
                    .setChatId(message.getChatId()));
        } else {
            userTasks = userTaskService.getUserTasks(message.getChatId());
            sender.execute(new SendMessage().setText("Активные таски чата:")
                    .setChatId(message.getChatId()));
        }
        if (userTasks != null) {
            for (UserTask task : userTasks) {
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton()
                        .setText("Удалить таску")
                        .setCallbackData("delete_task:" + task.getId()));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);

                String taskPlace = "";

                if (task.getTaskType().equals(UserTask.TaskType.SIMPLE)) {
                    taskPlace = task.getTargetPlace();
                } else {
                    taskPlace = task.getTargetPlace() + " по близости.";
                }

                sender.execute(new SendMessage().setText("\uD83D\uDCDD "
                        + task.getMessage() + "\n" + "\uD83D\uDEE3 Место: " + taskPlace + "\u200C")
                        .setChatId(message.getChatId()).setReplyMarkup(markupInline));
            }
        } else {
            sender.execute(new SendMessage().setText("Нет активных тасок:")
                    .setChatId(message.getChatId()));
        }
    }
}

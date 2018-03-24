package ru.holyway.georeminder.handler.message;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.pinnedmessages.PinChatMessage;
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
        if (userTask.getEventType().equals(UserTask.EventType.EVENT)) {
            eventTask(message, sender, userTask);
        } else {
            singleTask(message, sender, userTask);
        }
        userTaskService.addTask(userTask);
        userStateService.changeUserState(message.getFrom().getId(), UserState.NO_STATE);
    }

    private void singleTask(Message message, AbsSender sender, UserTask userTask) throws TelegramApiException {
        final StringBuilder taskTextMessage = new StringBuilder("✅  Задача создана\n");
        taskTextMessage.append("\uD83D\uDEE3  Место: ");
        if (userTask.getTaskType().equals(UserTask.TaskType.SIMPLE)) {
            taskTextMessage.append(userTask.getTargetPlace());
        } else {
            taskTextMessage.append(userTask.getTargetPlace()).append(" по близости");
        }
        taskTextMessage.append("\n\uD83D\uDCDD  Текст напоминания: ").append(userTask.getMessage());

        sender.execute(new SendMessage().setText(taskTextMessage.toString()).setChatId(message.getChatId()));
    }

    private void eventTask(Message message, AbsSender sender, UserTask userTask) throws TelegramApiException {
        final StringBuilder taskTextMessage = new StringBuilder("✅  Эвент создан\n");
        taskTextMessage.append("\uD83D\uDEE3  Место: ");
        taskTextMessage.append(userTask.getTargetPlace());

        taskTextMessage.append("\n\uD83D\uDCDD  Название: ").append(userTask.getMessage());

        List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Cancel");
        cancelButton.setCallbackData("event_cancel:" + userTask.getId());
        cancelButton.setText("✔  Завершить");
        buttons.add(cancelButton);
        buttonList.add(buttons);
        inlineKeyboardMarkup.setKeyboard(buttonList);

        Message newMessage = sender.execute(new SendMessage().setText(taskTextMessage.toString())
                .setReplyMarkup(inlineKeyboardMarkup)
                .setChatId(message.getChatId()));
        sender.execute(new PinChatMessage().setChatId(message.getChatId()).setMessageId(newMessage.getMessageId()));
    }
}

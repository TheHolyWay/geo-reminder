package ru.holyway.georeminder.handler.edit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.AddressLocation;
import ru.holyway.georeminder.entity.AddressResult;
import ru.holyway.georeminder.entity.PlaceRegion;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.PlaceTaskService;
import ru.holyway.georeminder.service.UserTaskService;
import utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class LocationEditMessageHandler implements EditMessageHandler {

    private final UserTaskService userTaskService;
    private final PlaceTaskService placeTaskService;

    private final static Logger LOGGER = LoggerFactory.getLogger(LocationEditMessageHandler.class);

    public LocationEditMessageHandler(UserTaskService userTaskService,
                                      PlaceTaskService placeTaskService) {
        this.userTaskService = userTaskService;
        this.placeTaskService = placeTaskService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        final Location location = message.getLocation();
        return location != null;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        final Location location = message.getLocation();
        if (message.getChat().isUserChat()) {
            final Integer userID = message.getFrom().getId();
            handleSimpleTasks(userTaskService.getSimpleUserTasks(userID), message, location, sender);
            handlePlaceTasks(userTaskService.getPlaceUserTasks(userID), message, location, sender);
        } else {
            final Long userID = message.getChatId();
            handleSimpleTasks(userTaskService.getSimpleUserTasks(userID), message, location, sender);
            handlePlaceTasks(userTaskService.getPlaceUserTasks(userID), message, location, sender);
        }

    }

    private void handleSimpleTasks(Set<UserTask> tasks, Message message, Location location,
                                   AbsSender sender) throws TelegramApiException {
        if (tasks != null) {
            for (UserTask userTask : tasks) {
                if (MathUtils.isNear(userTask.getLocation(), location)
                        && (userTask.getNotifyTime() == null || System.currentTimeMillis() > userTask.getNotifyTime())
                        && userTask.getChatID() != null && userTask.getChatID().equals(message.getChatId())) {
                    executeTask(userTask.getId(), userTask.getMessage(), userTask.getTargetPlace(), message, sender);
                    userTask.setNotifyTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
                    userTaskService.updateTask(userTask);
                }
            }
        }
    }

    private void handlePlaceTasks(Set<UserTask> tasks, Message message, Location location,
                                  AbsSender sender) throws TelegramApiException {
        for (UserTask task : tasks) {
            PlaceRegion regionForTask = placeTaskService.findPlaceByTask(task.getId());

            // Update region if it needed
            if (regionForTask == null) {
                placeTaskService.addRegionForTask(task.getId(), task.getTargetPlace(),
                        new AddressLocation(location.getLatitude(), location.getLongitude()));
            } else {
                regionForTask.updatePlaceRegion(new AddressLocation(location.getLatitude(), location.getLongitude()));
            }
            for (AddressResult addressResult : regionForTask.getPlacesInRegion()) {
                final Location targetLocation = addressResult.getGeometry().getLocation();
                LOGGER.info("Task ID {} Name {}", task.getId(), task.getTargetPlace());
                if (MathUtils.isNear(targetLocation, location)
                        && (task.getNotifyTime() == null || System.currentTimeMillis() > task.getNotifyTime())
                        && task.getChatID() != null && task.getChatID().equals(message.getChatId())) {
                    LOGGER.info("Task is near ID {} Name {}", task.getId(), task.getTargetPlace());
                    final String[] regxp = addressResult.getFormattedAddress().split(",");
                    final String smallAddress = regxp[0] + regxp[1];
                    final String placeMessage = addressResult.getName() + " на " + smallAddress;

                    executeTask(task.getId(), task.getMessage(), placeMessage, message, sender);
                    task.setNotifyTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
                    userTaskService.updateTask(task);
                }
            }
        }
    }

    private void executeTask(final String id,
                             final String userMessage,
                             final String targetPlace,
                             final Message message,
                             final AbsSender sender) throws TelegramApiException {
        List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton delayButton = new InlineKeyboardButton("Delay");
        delayButton.setText("\uD83D\uDD53  Отложить");
        delayButton.setCallbackData("delay:" + id);
        buttons.add(delayButton);
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Cancel");
        cancelButton.setCallbackData("cancel:" + id);
        cancelButton.setText("✔  Завершить");
        buttons.add(cancelButton);
        buttonList.add(buttons);
        final StringBuilder notifyMessage = new StringBuilder();
        notifyMessage.append("\uD83C\uDFC1 ");
        if (!message.getChat().isUserChat()) {
            final String userName = message.getFrom().getUserName() != null ? message.getFrom().getUserName() : message.getFrom().getFirstName();
            notifyMessage.append('@').append(userName).append(", ");
        }
        notifyMessage.append("Не забудьте ")
                .append(userMessage)
                .append(" пока вы рядом c ")
                .append(targetPlace);
        inlineKeyboardMarkup.setKeyboard(buttonList);
        sender.execute(new SendMessage().setText(notifyMessage.toString())
                .setReplyMarkup(inlineKeyboardMarkup)
                .setChatId(message.getChatId()));
    }
}

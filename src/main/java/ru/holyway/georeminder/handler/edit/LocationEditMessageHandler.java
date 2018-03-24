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
import ru.holyway.georeminder.entity.PlaceRegion;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.PlaceTaskService;
import ru.holyway.georeminder.service.UserTaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        handleSimpleTasks(userTaskService.getSimpleUserTasks(message.getFrom().getId()), message, location, sender);
        handlePlaceTasks(userTaskService.getPlaceUserTasks(message.getFrom().getId()), message, location, sender);
    }

    private void handleSimpleTasks(Set<UserTask> tasks, Message message, Location location,
                                   AbsSender sender) throws TelegramApiException {
        if (tasks != null) {
            for (UserTask userTask : tasks) {
                if (isNear(userTask.getLocation(), location)
                        && (userTask.getNotifyTime() == null || System.currentTimeMillis() > userTask.getNotifyTime())
                        && userTask.getChatID() != null && userTask.getChatID().equals(message.getChatId())) {
                    executeTask(userTask, message, sender);
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

            String str = "asd";
        }
    }

    private void executeTask(final UserTask userTask, final Message message, final AbsSender sender) throws TelegramApiException {
        List<List<InlineKeyboardButton>> buttonList = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton delayButton = new InlineKeyboardButton("Delay");
        delayButton.setText("\uD83D\uDD53  Отложить");
        delayButton.setCallbackData("delay:" + userTask.getId());
        buttons.add(delayButton);
        InlineKeyboardButton cancelButton = new InlineKeyboardButton("Cancel");
        cancelButton.setCallbackData("cancel:" + userTask.getId());
        cancelButton.setText("✔  Завершить");
        buttons.add(cancelButton);
        buttonList.add(buttons);
        inlineKeyboardMarkup.setKeyboard(buttonList);
        sender.execute(new SendMessage().setText("Не забудьте " + userTask.getMessage() + " пока вы рядом")
                .setReplyMarkup(inlineKeyboardMarkup)
                .setChatId(message.getChatId()));
    }

    protected boolean isNear(final Location target, final Location current) {
        double distance = sphericalDistance(target.getLongitude(), target.getLatitude(), current.getLongitude(), current.getLatitude());
        LOGGER.info("Distance between user and target is {} m", distance);
        return distance < 300;
    }

    private double sphericalDistance(double lon1, double lat1, double lon2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * 6371000;
    }
}

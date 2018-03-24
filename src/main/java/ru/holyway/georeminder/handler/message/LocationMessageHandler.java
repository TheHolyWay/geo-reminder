package ru.holyway.georeminder.handler.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.AddressResponse;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

import java.net.URI;

@Component
public class LocationMessageHandler implements MessageHandler {

    private final UserStateService userStateService;

    private final RestTemplate restTemplate;

    private final String googleApiKey;


    public LocationMessageHandler(UserStateService userStateService,
                                  @Value("${credential.google.apikey}") final String googleApiKey,
                                  RestTemplate restTemplate) {
        this.userStateService = userStateService;
        this.googleApiKey = googleApiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        if (UserState.ASK_LOCATION.equals(userStateService.getCurrentUserState(message.getFrom().getId()))) {
            final Location location = message.getLocation();
            return location != null;
        }
        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        final Location location = message.getLocation();
        sender.execute(new SendMessage().setText("\uD83D\uDCDD Опишите текст напоминания").setChatId(message.getChatId()));

        final String locRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + "," + location.getLongitude() + "&key=" + googleApiKey + "&language=ru";
        ResponseEntity<AddressResponse> addressResponse = restTemplate.getForEntity(URI.create(locRequest), AddressResponse.class);
        AddressResponse response = addressResponse.getBody();
        final String realAddress = response.getAddressResult().getFormattedAddress();
        final String[] rqxp = realAddress.split(",");
        final String smallAddress = rqxp[0] + rqxp[1];
        userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_MESSAGE);
        final UserTask userTask = userStateService.getDraftUserTask(message.getFrom().getId());
        userTask.setTaskType(UserTask.TaskType.SIMPLE);
        userTask.setLocation(location);
        if (message.getChat().isUserChat()) {
            userTask.setUserID(message.getFrom().getId());
        } else {
            userTask.setUserID(message.getChat().getId());
        }
        userTask.setChatID(message.getChatId());
        userTask.setTargetPlace(smallAddress);
        userStateService.changeDraftTask(message.getFrom().getId(), userTask);
    }
}

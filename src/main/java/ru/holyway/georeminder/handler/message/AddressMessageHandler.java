package ru.holyway.georeminder.handler.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.AddressLocation;
import ru.holyway.georeminder.entity.AddressResponse;
import ru.holyway.georeminder.entity.AddressResult;
import ru.holyway.georeminder.entity.UserTask;
import ru.holyway.georeminder.service.UserState;
import ru.holyway.georeminder.service.UserStateService;

import java.net.URI;
import java.net.URLEncoder;

@Component
public class AddressMessageHandler implements MessageHandler {

    private final UserStateService userStateService;
    private final String googleApiKey;
    private final RestTemplate restTemplate;

    public AddressMessageHandler(UserStateService userStateService,
                                 @Value("${credential.google.apikey}") final String googleApiKey,
                                 RestTemplate restTemplate) {
        this.userStateService = userStateService;
        this.googleApiKey = googleApiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
        if (UserState.ASK_LOCATION.equals(userStateService.getCurrentUserState(message.getFrom().getId()))) {
            return message.getText() != null && !message.getText().startsWith("/");
        }
        return false;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        final String locRequest = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + URLEncoder.encode(message.getText()) + "&key=" + googleApiKey + "&language=ru";

        ResponseEntity<AddressResponse> addressResponse =
                restTemplate.getForEntity(URI.create(locRequest), AddressResponse.class);

        AddressResponse response = addressResponse.getBody();
        AddressResult addressResult = response.getAddressResult();
        final String formattedAddress = addressResult.getFormattedAddress();
        final AddressLocation addressLocation = addressResult.getGeometry().getLocation();
        final String[] rqxp = formattedAddress.split(",");
        final String smallAddress = rqxp[0] + rqxp[1];
        final UserTask userTask = userStateService.getDraftUserTask(message.getFrom().getId());
        userTask.setTaskType(UserTask.TaskType.SIMPLE);
        userTask.setLocation(addressLocation);
        if (message.getChat().isUserChat()) {
            userTask.setUserID(message.getFrom().getId());
        } else {
            userTask.setUserID(message.getChat().getId());
        }
        userTask.setChatID(message.getChatId());
        userTask.setTargetPlace(smallAddress);
        userStateService.changeDraftTask(message.getFrom().getId(), userTask);

        sender.execute(new SendMessage().setText("❓ Вы имели в виду следующий адрес:\n" + formattedAddress).setChatId(message.getChatId()));
        userStateService.changeUserState(message.getFrom().getId(), UserState.ASK_APPROVE_ADDRESS);
    }
}

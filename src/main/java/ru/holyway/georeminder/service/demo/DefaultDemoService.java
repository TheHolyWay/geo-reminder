package ru.holyway.georeminder.service.demo;

import org.powermock.api.mockito.PowerMockito;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.entity.AddressLocation;
import ru.holyway.georeminder.handler.edit.LocationEditMessageHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Profile("dev")
public class DefaultDemoService implements DemoService {

    private Map<Integer, DemoTask> userDemoTasks = new HashMap<>();

    private final LocationEditMessageHandler locationEditMessageHandler;
    private final TaskScheduler taskScheduler;

    public DefaultDemoService(LocationEditMessageHandler locationEditMessageHandler) {
        this.locationEditMessageHandler = locationEditMessageHandler;
        this.taskScheduler = new ConcurrentTaskScheduler();
    }

    @Override
    public DemoTask demoTask(Integer userId) {
        if (!userDemoTasks.containsKey(userId)) {
            DemoTask demoTask = new DemoTask();
            demoTask.setDemoState(DemoState.IDLE);
            userDemoTasks.put(userId, demoTask);
        }
        return userDemoTasks.get(userId);
    }

    @Override
    public void firstLocation(Integer userId, Location location) {
        DemoTask demoTask = demoTask(userId);
        demoTask.setFirstLocation(location);
    }

    @Override
    public void secondLocation(Integer userId, Location location) {
        DemoTask demoTask = demoTask(userId);
        demoTask.setSecondLocation(location);
    }

    @Override
    public void startTask(Integer userId, AbsSender sender) {
        DemoTask demoTask = demoTask(userId);
        Location first = demoTask.getFirstLocation();
        Location second = demoTask.getSecondLocation();

        Float latitudeDelta = second.getLatitude() - first.getLatitude();
        Float longitudeDelta = second.getLongitude() - first.getLongitude();

        final Float latitudeStep = latitudeDelta / 3;
        final Float longitudeStep = longitudeDelta / 3;

        long delay = TimeUnit.SECONDS.toMillis(5);

        final AddressLocation currentLocation = new AddressLocation();
        currentLocation.setLat(first.getLatitude());
        currentLocation.setLng(first.getLongitude());

        ScheduledFuture future = taskScheduler.scheduleWithFixedDelay(() -> {
            currentLocation.setLat(currentLocation.getLatitude() + latitudeStep);
            currentLocation.setLng(currentLocation.getLongitude() + longitudeStep);

            User user = PowerMockito.mock(User.class);
            PowerMockito.when(user.getId()).thenReturn(userId);

            Message message = PowerMockito.mock(Message.class);
            PowerMockito.when(message.getFrom()).thenReturn(user);
            PowerMockito.when(message.getChatId()).thenReturn(demoTask.getChatId());
            PowerMockito.when(message.getLocation()).thenReturn(currentLocation);

            Chat chat = PowerMockito.mock(Chat.class);
            PowerMockito.when(message.getChat()).thenReturn(chat);
            PowerMockito.when(chat.isUserChat()).thenReturn(true);

            SendLocation sendLocation = new SendLocation().setChatId(demoTask.getChatId())
                    .setLatitude(currentLocation.getLatitude())
                    .setLongitude(currentLocation.getLongitude());

            try {
                locationEditMessageHandler.execute(message, sender);
                sender.execute(sendLocation);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }, delay);

        demoTask.setScheduledFuture(future);
    }

    @Override
    public void stopTask(Integer userId) {
        demoTask(userId).getScheduledFuture().cancel(true);
        userDemoTasks.remove(userId);
    }
}
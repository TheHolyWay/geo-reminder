package ru.holyway.georeminder.service.demo;

import org.telegram.telegrambots.api.objects.Location;

import java.util.concurrent.ScheduledFuture;

public class DemoTask {

    private Integer userId;
    private Long chatId;
    private Location firstLocation;
    private Location secondLocation;
    private DemoState demoState;
    private ScheduledFuture scheduledFuture;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Location getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }

    public Location getSecondLocation() {
        return secondLocation;
    }

    public void setSecondLocation(Location secondLocation) {
        this.secondLocation = secondLocation;
    }

    public DemoState getDemoState() {
        return demoState;
    }

    public void setDemoState(DemoState demoState) {
        this.demoState = demoState;
    }

    public ScheduledFuture getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}

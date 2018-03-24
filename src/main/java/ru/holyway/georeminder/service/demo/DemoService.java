package ru.holyway.georeminder.service.demo;

import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.bots.AbsSender;

public interface DemoService {

    DemoTask demoTask(Integer userId);
    void firstLocation(Integer userId, Location location);
    void secondLocation(Integer userId, Location location);
    void startTask(Integer userId, AbsSender sender);
    void stopTask(Integer userId);
}

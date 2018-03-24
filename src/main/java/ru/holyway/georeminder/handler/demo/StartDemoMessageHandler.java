package ru.holyway.georeminder.handler.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.handler.message.MessageHandler;
import ru.holyway.georeminder.service.demo.DemoService;
import ru.holyway.georeminder.service.demo.DemoState;

@Component
@Profile("dev")
public class StartDemoMessageHandler implements MessageHandler {

    private static final String START = "/start_demo";

    private DemoService demoService;

    public StartDemoMessageHandler(DemoService demoService) {
        this.demoService = demoService;
    }


    @Override
    public boolean isNeedToHandle(Message message) {
            return message.hasText() &&
                START.equalsIgnoreCase(message.getText()) &&
                demoService.demoTask(message.getFrom().getId()).getDemoState() == DemoState.READY;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        demoService.startTask(message.getFrom().getId(), sender);
        demoService.demoTask(message.getFrom().getId()).setDemoState(DemoState.RUNNING);
    }
}

package ru.holyway.georeminder.handler.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.handler.message.MessageHandler;
import ru.holyway.georeminder.service.MessageSenderService;
import ru.holyway.georeminder.service.demo.DemoService;
import ru.holyway.georeminder.service.demo.DemoState;
import ru.holyway.georeminder.service.demo.DemoTask;

@Component
@Profile("dev")
public class CreateDemoMessageHandler implements MessageHandler {

    private static final String CREATE = "/new_demo";

    private DemoService demoService;

    public CreateDemoMessageHandler(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {
            return message.hasText() &&
                CREATE.equalsIgnoreCase(message.getText()) &&
                demoService.demoTask(message.getFrom().getId()).getDemoState() == DemoState.IDLE;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        DemoTask demoTask = demoService.demoTask(message.getFrom().getId());
        demoTask.setUserId(message.getFrom().getId());
        demoTask.setChatId(message.getChatId());
        demoTask.setDemoState(DemoState.ASK_START_LOCATION);
        demoTask.setFirstLocation(null);
        demoTask.setFirstLocation(null);

        SendMessage sendMessage = new SendMessage()
                .setText("Ок, отправь мне начальную локацию")
                .setChatId(message.getChatId());
        sender.execute(sendMessage);
    }
}

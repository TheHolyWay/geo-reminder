package ru.holyway.georeminder.handler.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.handler.message.MessageHandler;
import ru.holyway.georeminder.service.demo.DemoService;
import ru.holyway.georeminder.service.demo.DemoState;
import ru.holyway.georeminder.service.demo.DemoTask;

@Component
@Profile("dev")
public class DemoDefineLocationMessageHandler implements MessageHandler {

    private DemoService demoService;

    public DemoDefineLocationMessageHandler(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public boolean isNeedToHandle(Message message) {

        return
                //location && state_ask_start_location
                message.hasLocation() &&
                demoService.demoTask(message.getFrom().getId()).getDemoState()
                        == DemoState.ASK_START_LOCATION ||

                //location && state_ask_end_location
                message.hasLocation() &&
                demoService.demoTask(message.getFrom().getId()).getDemoState()
                        == DemoState.ASK_END_LOCATION;
    }

    @Override
    public void execute(Message message, AbsSender sender) throws TelegramApiException {
        DemoTask demoTask = demoService.demoTask(message.getFrom().getId());

        if (demoTask.getFirstLocation() == null) {
            demoTask.setFirstLocation(message.getLocation());
            SendMessage sendMessage = new SendMessage()
                    .setText("Ок, отправь мне конечную локацию")
                    .setChatId(message.getChatId());
            sender.execute(sendMessage);
        } else if (demoTask.getSecondLocation() == null) {
            demoTask.setSecondLocation(message.getLocation());
            demoTask.setDemoState(DemoState.READY);
            SendMessage sendMessage = new SendMessage()
                    .setText("Демо готово к запуску!")
                    .setChatId(message.getChatId());
            sender.execute(sendMessage);
        }
    }
}

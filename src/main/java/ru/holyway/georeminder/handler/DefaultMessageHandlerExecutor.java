package ru.holyway.georeminder.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.holyway.georeminder.handler.callback.CallbackHandler;
import ru.holyway.georeminder.handler.edit.EditMessageHandler;
import ru.holyway.georeminder.handler.message.MessageHandler;

import java.util.List;

@Component
public class DefaultMessageHandlerExecutor implements MessageHandlerExecutor {

    private final List<CallbackHandler> callbackHandlers;

    private final List<EditMessageHandler> editMessageHandlers;

    private final List<MessageHandler> messageHandlers;

    public DefaultMessageHandlerExecutor(final List<CallbackHandler> callbackHandlers,
                                         final List<EditMessageHandler> editMessageHandlers,
                                         final List<MessageHandler> messageHandlers) {
        this.callbackHandlers = callbackHandlers;
        this.editMessageHandlers = editMessageHandlers;
        this.messageHandlers = messageHandlers;
    }

    @Override
    public void execute(final Update update, final AbsSender sender) {
        try {
            if (update.hasCallbackQuery()) {
                for (CallbackHandler callbackHandler : callbackHandlers) {
                    if (callbackHandler.isNeedToHandle(update.getCallbackQuery())) {
                        callbackHandler.execute(update.getCallbackQuery(), sender);
                        return;
                    }
                }
            } else if (update.hasEditedMessage()) {
                for (EditMessageHandler editMessageHandler : editMessageHandlers) {
                    if (editMessageHandler.isNeedToHandle(update.getEditedMessage())) {
                        editMessageHandler.execute(update.getEditedMessage(), sender);
                        return;
                    }
                }
            } else if (update.hasMessage()) {
                for (MessageHandler messageHandler : messageHandlers) {
                    if (messageHandler.isNeedToHandle(update.getMessage())) {
                        messageHandler.execute(update.getMessage(), sender);
                        return;
                    }
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}

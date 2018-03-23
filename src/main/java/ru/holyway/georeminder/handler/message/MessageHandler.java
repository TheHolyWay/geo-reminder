package ru.holyway.georeminder.handler.message;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 *
 */
public interface MessageHandler {

    /**
     * @param message
     * @return
     */
    boolean isNeedToHandle(final Message message);

    /**
     * @param message
     */
    void execute(final Message message, final AbsSender sender) throws TelegramApiException;
}

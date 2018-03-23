package ru.holyway.georeminder.handler.callback;

import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public interface CallbackHandler {

    /**
     * @param callbackQuery
     * @return
     */
    boolean isNeedToHandle(final CallbackQuery callbackQuery);

    /**
     * @param callbackQuery
     */
    void execute(final CallbackQuery callbackQuery, final AbsSender sender) throws TelegramApiException;
}

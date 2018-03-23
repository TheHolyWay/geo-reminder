package ru.holyway.georeminder.handler;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

/**
 *
 */
public interface MessageHandlerExecutor {

    /**
     * @param update
     */
    void execute(final Update update, final AbsSender sender);

}

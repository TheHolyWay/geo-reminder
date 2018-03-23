package ru.holyway.georeminder.service;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Message;

/**
 *
 */
public interface MessageSenderService {

    /**
     *
     * @param message
     * @return
     */
    Message sendMessage(final BotApiMethod<Message> message);

    /**
     *
     * @param message
     * @param chatId
     * @return
     */
    Message sendMessage(final String message, final String chatId);
}

package ru.holyway.georeminder.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.bots.AbsSender;

@Component
public class DefaultMessageSenderService implements MessageSenderService {

    private final AbsSender sender;

    public DefaultMessageSenderService(AbsSender sender) {
        this.sender = sender;
    }

    @Override
    public Message sendMessage(BotApiMethod<Message> message) {
        return null;
    }

    @Override
    public Message sendMessage(String message, String chatId) {
        return null;
    }
}

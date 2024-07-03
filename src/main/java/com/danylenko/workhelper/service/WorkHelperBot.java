package com.danylenko.workhelper.service;

import com.danylenko.workhelper.configuration.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


@Component
public class WorkHelperBot extends TelegramLongPollingBot {

    private final MessageHandler messageHandler;

    private final KeyboardService keyboardService;
    private final BotConfig botConfig;
    private final MessageService messageService;

    @Autowired
    public WorkHelperBot(MessageHandler messageHandler, KeyboardService keyboardService, BotConfig botConfig, @Lazy MessageService messageService) {
        this.messageHandler = messageHandler;
        this.keyboardService = keyboardService;
        this.botConfig = botConfig;
        this.messageService = messageService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String responseText = messageHandler.handleTextMessage(update);
            messageService.sendMessage(chatId, responseText, true);
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }
}

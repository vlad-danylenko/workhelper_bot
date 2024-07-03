package com.danylenko.workhelper.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;


@Service
@Slf4j
public class MessageHandlerImpl implements MessageHandler {

    @Autowired
    @Lazy
    private NotificationService notificationService;

    @Override
    public String handleTextMessage(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();
        log.info("{} {} sent text: \"{}\"",userId,username,messageText);
        String responseText = "";

        if (messageText.equals("/start")) {
            return "Ласкаво просимо! Використовуйте кнопки нижче для взаємодії з ботом.";
        }

        if (messageText.equals("Перевірити statusCode аукціону")) {
            log.info("{} {} is checking auction status", userId, username);
            responseText = notificationService.manualCheckApiResponse();
        } else responseText = "Невідома команда";

        return responseText;
    }


}


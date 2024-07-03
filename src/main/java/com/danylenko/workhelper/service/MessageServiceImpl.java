package com.danylenko.workhelper.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



@Service
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final WorkHelperBot workHelperBot;
    private final KeyboardService keyboardService;

    @Autowired
    public MessageServiceImpl(@Lazy WorkHelperBot workHelperBot, KeyboardService keyboardService) {
        this.workHelperBot = workHelperBot;
        this.keyboardService = keyboardService;
    }
    @Override
    public void sendMessage(long chatId, String responseText, boolean keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(responseText);
        if (keyboard) {
            message.setReplyMarkup(keyboardService.getMainMenuKeyboard());
        }

        try {
            workHelperBot.execute(message);
            log.info("Message \"{}\" was sent to {}",responseText,chatId);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

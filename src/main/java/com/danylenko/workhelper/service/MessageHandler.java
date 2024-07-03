package com.danylenko.workhelper.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageHandler {
    String handleTextMessage(Update update);
}

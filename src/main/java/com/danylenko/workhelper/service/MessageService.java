package com.danylenko.workhelper.service;

public interface MessageService {
    void sendMessage(long chatId, String responseText, boolean keyboard);
}

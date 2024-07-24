package com.danylenko.workhelper.service;

import com.danylenko.workhelper.entity.MonoClient;

public interface MonobankService {
    String checkMonoBalance(long userId);
    void addApiKey(long userId, String apiKey);
    boolean isValidApiKey(String apiKey);
    String checkSpendsCurrentMonth(long userId);
    MonoClient getTelegramClientInfo(String apiKey);
}

package com.danylenko.workhelper.service;

public interface MonobankService {
    String checkMonoBalance(long userId);
    void addApiKey(long userId, String apiKey);
    boolean isValidApiKey(String apiKey);
}

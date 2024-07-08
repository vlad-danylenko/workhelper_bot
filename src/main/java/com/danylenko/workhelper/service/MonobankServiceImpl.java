package com.danylenko.workhelper.service;

import com.danylenko.workhelper.model.TelegramKey;
import com.danylenko.workhelper.repository.ObjectCountRepository;
import com.danylenko.workhelper.repository.TelegramKeyRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class MonobankServiceImpl implements MonobankService {
    private static final String API_URL = "https://api.monobank.ua/personal/client-info";
    @Autowired
    private MessageService messageService;

    private final TelegramKeyRepository telegramKeyRepository;

    public MonobankServiceImpl(TelegramKeyRepository telegramKeyRepository) {
        this.telegramKeyRepository = telegramKeyRepository;
    }


    public String checkMonoBalance(long userId) {
        String formattedBalance = null;
        int platinumBalance = 0;
        int totalSum = 0;
        String apiKey = defineApiKey(userId);
        StringBuilder responseBuilder = new StringBuilder("Поточний баланс по гривневих картках:\n");
        if (apiKey == null) {
            System.out.println("Заходимо в іф");
            System.out.println("No API key found for user ID: " + userId);
            return formattedBalance = "Ви не додали свій ключ. Відвідайте сайт https://api.monobank.ua/ та надішліть свій токен";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            request.addHeader("X-TOKEN", apiKey);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
                JsonArray accounts = jsonObject.getAsJsonArray("accounts");

                int creditBalance = 0;
                boolean platinumAccountFound = false;


                for (int i = 0; i < accounts.size(); i++) {
                    JsonObject account = accounts.get(i).getAsJsonObject();
                    String type = account.get("type").getAsString();
                    int currencyCode = account.get("currencyCode").getAsInt();

                    if (currencyCode == 980) {
                        int balance = account.get("balance").getAsInt();
                        totalSum += balance;
                        responseBuilder.append(type)
                                .append(": ")
                                .append(formatBalance(balance))
                                .append("\n");
                    }


//                    if ("platinum".equalsIgnoreCase(type)) {
//                        platinumBalance = account.get("balance").getAsInt();
//                        //creditBalance = account.get("creditLimit").getAsInt();
//                        platinumAccountFound = true;
//                        break;
//                    }
                }
                formattedBalance = formatBalance(totalSum);
                responseBuilder.append("\nЗагальний баланс по всім карткам: ")
                        .append(formattedBalance);

//                if (platinumAccountFound) {
//                    // platinumBalance -= creditBalance;
//                    formattedBalance = formatBalance(platinumBalance);
//                    System.out.println("Platinum Account Balance: " + formattedBalance);
//                } else {
//                    System.out.println("No platinum account found");
//                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
//        return "Поточний баланс по всім UAH картам: " + formattedBalance;
        return responseBuilder.toString();
    }

    @Override
    public void addApiKey(long userId, String apiKey) {
        if (!isValidApiKey(apiKey)) {
            log.info("Invalid API key format: " + apiKey);
            throw new IllegalArgumentException("Invalid API key format");
        }
        TelegramKey telegramKey = new TelegramKey();
        telegramKey.setUserId(String.valueOf(userId));
        telegramKey.setApiKey(apiKey);
        telegramKeyRepository.save(telegramKey);
        log.info("API key for user {} has been added/updated.", userId);
    }

    @Override
    public boolean isValidApiKey(String apiKey) {
        return apiKey != null && apiKey.length() >= 10 && apiKey.matches("^[a-zA-Z0-9_-]+$");
    }

    private String formatBalance(int balance) {
        String balanceStr = String.valueOf(Math.abs(balance));
        StringBuilder formatted = new StringBuilder();
        int length = balanceStr.length();

        for (int i = 0; i < length; i++) {
            formatted.append(balanceStr.charAt(i));
            if (length == 8 && i == 2 || length == 7 && i == 1 || length == 6 && i == 0) {
                formatted.append(' ');
            }
             else if (length == 8 && i == 5 || length == 7 && i == 4 || length == 6 && i == 3 || length == 5 && i == 2 || length == 4 && i == 1 || length == 3 && i == 0) {
                formatted.append(',');
            }
        }

        if (balance < 0) {
            formatted.insert(0, '-');
        }

        return formatted.toString();
    }

    private String defineApiKey(long userId) {
        TelegramKey telegramKey = telegramKeyRepository.findByUserId(String.valueOf(userId));
        return telegramKey != null ? telegramKey.getApiKey() : null;
    }


}

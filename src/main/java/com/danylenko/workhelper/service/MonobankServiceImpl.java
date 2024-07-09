package com.danylenko.workhelper.service;

import com.danylenko.workhelper.model.TelegramKey;
import com.danylenko.workhelper.repository.TelegramKeyRepository;
import com.danylenko.workhelper.service.enums.CardType;
import com.danylenko.workhelper.service.util.EpochConverter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    @Autowired
    private EpochConverter epochConverter;

    private final TelegramKeyRepository telegramKeyRepository;

    public MonobankServiceImpl(TelegramKeyRepository telegramKeyRepository) {
        this.telegramKeyRepository = telegramKeyRepository;
    }


    public String checkMonoBalance(long userId) {
        String formattedBalance = null;
        int totalSum = 0;
        String apiKey = defineApiKey(userId);
        StringBuilder responseBuilder = new StringBuilder("Поточний баланс по гривневих картках:\n");
        if (apiKey == null) {
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

                for (int i = 0; i < accounts.size(); i++) {
                    JsonObject account = accounts.get(i).getAsJsonObject();
                    String cardTypeApi = account.get("type").getAsString();
                    String cardType = CardType.getDisplayName(cardTypeApi);
                    int currencyCode = account.get("currencyCode").getAsInt();
                    int creditLimit = account.get("creditLimit").getAsInt();

                    if (currencyCode == 980) {
                        int balance = account.get("balance").getAsInt();
                        if (creditLimit > 0) {
                            balance -= creditLimit;
                        }
                        totalSum += balance;
                        responseBuilder.append(cardType)
                                .append(": ")
                                .append(formatBalance(balance))
                                .append(" грн.")
                                .append("\n");
                    }

                }
                formattedBalance = formatBalance(totalSum);
                responseBuilder.append("\nЗагальний баланс по всім карткам: ")
                        .append(formattedBalance)
                        .append(" грн.");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
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

    public String checkSpendsCurrentMonth (long userId) {
        String formattedBalance = null;
        int totalSum = 0;
        String apiKey = defineApiKey(userId);
        long timestamp = epochConverter.getTimeFromFirstDayOfMonth();
        String apiSpendUrl = "https://api.monobank.ua/personal/statement/0/" + timestamp;
        log.info("Generated link: {}",apiSpendUrl);
        StringBuilder responseBuilder = new StringBuilder("Сума витрат в цьому місяці:\n");
        if (apiKey == null) {
            System.out.println("No API key found for user ID: " + userId);
            return formattedBalance = "Ви не додали свій ключ. Відвідайте сайт https://api.monobank.ua/ та надішліть свій токен";
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiSpendUrl);
            request.addHeader("X-TOKEN", apiKey);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonArray operations = new Gson().fromJson(jsonResponse, JsonArray.class);


                for (int i = 0; i < operations.size(); i++) {
                    JsonObject operation = operations.get(i).getAsJsonObject();
                    int operationAmount = operation.get("operationAmount").getAsInt();
                    totalSum += operationAmount;
                }
                formattedBalance = formatBalance(totalSum);
                responseBuilder
                        .append(formattedBalance)
                        .append(" грн.");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return responseBuilder.toString();
    }


}

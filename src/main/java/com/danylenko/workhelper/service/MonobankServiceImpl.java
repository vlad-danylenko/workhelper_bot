package com.danylenko.workhelper.service;

import com.danylenko.workhelper.model.MonoKey;
import com.danylenko.workhelper.model.MonoTransaction;
import com.danylenko.workhelper.model.MonoClient;
import com.danylenko.workhelper.repository.MonoTransactionRepository;
import com.danylenko.workhelper.repository.MonoClientRepository;
import com.danylenko.workhelper.repository.MonoKeyRepository;
import com.danylenko.workhelper.service.enums.CardType;
import com.danylenko.workhelper.service.util.BalanceFormat;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MonobankServiceImpl implements MonobankService {
    private static final String API_URL = "https://api.monobank.ua/personal/client-info";
    private final EpochConverter epochConverter;
    private final MonoKeyRepository monoKeyRepository;
    private final MonoClientRepository monoClientRepository;
    private final MonoTransactionRepository monoTransactionRepository;

    private final BalanceFormat balanceFormat;

    @Autowired
    public MonobankServiceImpl(EpochConverter epochConverter, MonoKeyRepository monoKeyRepository, MonoClientRepository monoClientRepository, MonoTransactionRepository monoTransactionRepository, BalanceFormat balanceFormat) {
        this.epochConverter = epochConverter;
        this.monoKeyRepository = monoKeyRepository;
        this.monoClientRepository = monoClientRepository;
        this.monoTransactionRepository = monoTransactionRepository;
        this.balanceFormat = balanceFormat;
    }

    public String checkMonoBalance(long userId) {
        String apiKey = defineApiKey(userId);
        if (apiKey == null) {
            log.warn("No API key found for user ID: {}",userId);
            return "Ви не додали свій ключ. Відвідайте сайт https://api.monobank.ua/ та надішліть свій токен";
        }
        StringBuilder responseBuilder = new StringBuilder("Поточний баланс по гривневих картках:\n");
        int totalSum = 0;

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
                                .append(balanceFormat.formatBalance(balance))
                                .append(" грн.")
                                .append("\n");
                    }

                }
                responseBuilder.append("\nЗагальний баланс по всім карткам: ")
                        .append(balanceFormat.formatBalance(totalSum))
                        .append(" грн.");
            }
        } catch (IOException | ParseException e) {
            log.error("Error checking balance for user ID: {}", userId, e);
        }
        return responseBuilder.toString();
    }

    public String checkSpendsCurrentMonth(long userId) {
        String apiKey = defineApiKey(userId);
        if (apiKey == null) {
            log.warn("No API key found for user ID: {}", userId);
            return "Ви не додали свій ключ. Відвідайте сайт https://api.monobank.ua/ та надішліть свій токен";
        }
        long timestamp = epochConverter.getTimeFromFirstDayOfMonth();
        String apiSpendUrl = "https://api.monobank.ua/personal/statement/0/" + timestamp;
        log.info("Generated link: {}", apiSpendUrl);
        Map<String, MonoTransaction> transactions = new HashMap<>();
        StringBuilder responseBuilder = new StringBuilder("Сума витрат в цьому місяці: ");
        int totalSpendSum = 0;
        int totalTopUpSum = 0;

        // Find TelegramClient by userId
        MonoKey monoKey = monoKeyRepository.findById(String.valueOf(userId)).orElse(null);
        if (monoKey == null) {
            log.warn("No TelegramKey found for user ID: {}", userId);
        }

        MonoClient monoClient = monoKey.getMonoClient();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(apiSpendUrl);
            request.addHeader("X-TOKEN", apiKey);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonArray operations = new Gson().fromJson(jsonResponse, JsonArray.class);


                for (int i = 0; i < operations.size(); i++) {
                    JsonObject operation = operations.get(i).getAsJsonObject();
                    int operationAmount = operation.get("operationAmount").getAsInt(); // assuming the correct field name is "amount"
                    String operationId = operation.get("id").getAsString();
                    LocalDateTime transactionTime = LocalDateTime.ofEpochSecond(operation.get("time").getAsLong(), 0, ZoneOffset.UTC).plusHours(3);


                    // Create and store MonoTransaction in the map
                    MonoTransaction monoTransaction = new MonoTransaction();
                    monoTransaction.setMonoClient(monoClient.getClientId());
                    monoTransaction.setOperationId(operationId);
                    monoTransaction.setTransactionTime(transactionTime);
                    monoTransaction.setOperationAmount(operationAmount);

                    transactions.put(operationId, monoTransaction);

                    if (operationAmount < 0 ) {
                        totalSpendSum += operationAmount;
                    } else if (operationAmount > 0) {
                        totalTopUpSum += operationAmount;
                    }
                }
                monoTransactionRepository.saveAll(transactions.values());
                responseBuilder.append(balanceFormat.formatBalance(totalSpendSum))
                        .append(" грн.\n")
                        .append("Поповнень карти у цьому місяці: ")
                        .append(balanceFormat.formatBalance(totalTopUpSum))
                        .append(" грн.");
            }
        } catch (IOException | ParseException e) {
            log.error("Error checking spends for user ID: {}", userId, e);
        }
        return responseBuilder.toString();
    }

    @Override
    public void addApiKey(long userId, String apiKey) {
        if (!isValidApiKey(apiKey)) {
            log.info("Invalid API key format: " + apiKey);
            throw new IllegalArgumentException("Invalid API key format");
        }
        MonoClient monoClient = getTelegramClientInfo(apiKey);
        monoClientRepository.save(monoClient);

        MonoKey monoKey = new MonoKey();
        monoKey.setUserId(String.valueOf(userId));
        monoKey.setApiKey(apiKey);
        monoKey.setMonoClient(monoClient);
        monoKeyRepository.save(monoKey);
        log.info("API key for user {} has been added/updated with client ID: {} and name: {}", userId, monoClient.getClientId(), monoClient.getName());
    }

    @Override
    public MonoClient getTelegramClientInfo(String apiKey) {
        MonoClient monoClient = new MonoClient();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            request.addHeader("X-TOKEN", apiKey);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);

                monoClient.setClientId(jsonObject.get("clientId").getAsString());
                monoClient.setName(jsonObject.get("name").getAsString());
            }
        } catch (IOException | ParseException e) {
            log.error("Error retrieving client information for API key: {}", apiKey, e);
            throw new RuntimeException("Failed to retrieve client information", e);
        }
        return monoClient;
    }

    @Override
    public boolean isValidApiKey(String apiKey) {
        return apiKey != null && apiKey.length() >= 10 && apiKey.matches("^[a-zA-Z0-9_-]+$");
    }

    private String defineApiKey(long userId) {
        MonoKey monoKey = monoKeyRepository.findByUserId(String.valueOf(userId));
        return monoKey != null ? monoKey.getApiKey() : null;
    }




}

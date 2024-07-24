package com.danylenko.workhelper.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class MessageHandlerImpl implements MessageHandler {
    private List<Long> usersWaitingApiKey = new ArrayList<>();

    @Autowired
    @Lazy
    private ProzorroService prozorroService;
    @Autowired
    private MonobankService monobankService;

    @Override
    public String handleTextMessage(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        String username = update.getMessage().getFrom().getUserName();
        log.info("{} {} sent text: \"{}\"",userId,username,messageText);


        if (usersWaitingApiKey.contains(userId) && !messageText.equals("/stop")) {
            return handleApiKeyMessage(userId, messageText);
        }

        switch (messageText) {
            case "/start":
                return "Ласкаво просимо! Використовуйте кнопки нижче для взаємодії з ботом.";
//            case "Перевірити statusCode аукціону":
//                return checkAuctionStatus(userId, username);
            case "largePrivatization-english":
                return checkPrivatizationResponse(userId, username);
            case "Перевірити к-сть обʼєктів":
                return checkObjectCount();
            case "Перевірити поточний баланс":
                return checkMonoBalance(userId);
            case "Витрати в поточному місяці":
                return checkSpendsCurrentMonth(userId);
            case "/stop":
                usersWaitingApiKey.remove(userId);
                return "Операцію по додаванню ключа скасовано";
            default:
                return "Невідома команда";
        }
    }

    private String handleApiKeyMessage(long userId, String messageText) {
        try {
            monobankService.addApiKey(userId, messageText);
            usersWaitingApiKey.remove(userId);
            return "Ключ успішно доданий.";
        } catch (IllegalArgumentException e) {
            return "Невірний формат ключа. Будь ласка, спробуйте ще раз. Або надішліть /stop якщо не бажаєте продовжувати";
        }
    }

//    private String checkAuctionStatus(long userId, String username) {
//        log.info("{} {} is checking auction status", userId, username);
//        return prozorroService.manualCheckApiResponse();
//    }
    private String checkPrivatizationResponse (long userId, String username) {
        log.info("{} {} is checking large privatization response", userId, username);
        return prozorroService.manualCheckApiPayload();
    }

    private String checkObjectCount() {
        LocalDate startDate = LocalDate.of(2022, 1, 1);
        //LocalDate endDate = LocalDate.now();
        LocalDate endDate = LocalDate.of(2022, 2, 1);

        List<String> dates = startDate.datesUntil(endDate.plusDays(1))
                .map(LocalDate::toString)
                .collect(Collectors.toList());
        return "Кількість обʼєктів станом на " + endDate + ": " + prozorroService.getTotalObjectCount(dates);
    }

    private String checkMonoBalance(long userId) {
        String responseText = monobankService.checkMonoBalance(userId);
        if (responseText.contains("Ви не додали свій ключ.")) {
            usersWaitingApiKey.add(userId);
        }
        return responseText;
    }

    private String checkSpendsCurrentMonth(long userId) {
        String responseText = monobankService.checkSpendsCurrentMonth(userId);
        if (responseText.contains("Ви не додали свій ключ.")) {
            usersWaitingApiKey.add(userId);
        }
        return responseText;
    }

}


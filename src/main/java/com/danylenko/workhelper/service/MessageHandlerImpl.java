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
        String responseText = "";

        if (messageText.equals("/start")) {
            return "Ласкаво просимо! Використовуйте кнопки нижче для взаємодії з ботом.";
        }

        if (messageText.equals("Перевірити statusCode аукціону")) {
            log.info("{} {} is checking auction status", userId, username);
            responseText = prozorroService.manualCheckApiResponse();
        }

        if (messageText.equals("Перевірити к-сть обʼєктів")) {
            LocalDate startDate = LocalDate.of(2022, 1, 1);
            //LocalDate endDate = LocalDate.now();
            LocalDate endDate = LocalDate.of(2022,2,1);

            List<String> dates = startDate.datesUntil(endDate.plusDays(1))
                    .map(LocalDate::toString)
                    .collect(Collectors.toList());
            responseText = "Кількість обʼєктів станом на " + String.valueOf(endDate) + ": " + String.valueOf(prozorroService.getTotalObjectCount(dates));
        } //else responseText = "Невідома команда";

        if (usersWaitingApiKey.contains(userId)) {
            try {
                monobankService.addApiKey(userId, messageText);
                responseText = "Ключ успішно доданий.";
                usersWaitingApiKey.remove(userId);
            } catch (IllegalArgumentException e) {
                responseText = "Невірний формат ключа. Будь ласка, спробуйте ще раз. " +
                        "Або надішліть /stop якщо не бажаєте продовжувати";
            }
        }

        if (messageText.equals("Перевірити поточний баланс")) {
            responseText = monobankService.checkMonoBalance(userId);
            if (responseText.contains("Ви не додали свій ключ.")) {
                usersWaitingApiKey.add(userId);
            }
        }



        if (messageText.equals("/stop")) {
            usersWaitingApiKey.remove(userId);
            responseText = "Операцію по додаванню ключа скасовано";
        }

        return responseText;
    }


}


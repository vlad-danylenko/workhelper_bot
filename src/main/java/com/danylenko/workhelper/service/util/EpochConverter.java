package com.danylenko.workhelper.service.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
public class EpochConverter {
    public long getTimeFromFirstDayOfMonth() {
        // Отримуємо поточну дату
        LocalDate currentDate = LocalDate.now();

        // Визначаємо перше число поточного місяця
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);

        // Конвертуємо LocalDate до Instant (припускаючи, що це початок дня у UTC)
        Instant instant = firstDayOfMonth.atStartOfDay().toInstant(ZoneOffset.UTC);

        // Отримуємо epoch timestamp в секундах
        long epochSeconds = instant.getEpochSecond();

        // Отримуємо epoch timestamp в мілісекундах
        return instant.toEpochMilli();
    }
}

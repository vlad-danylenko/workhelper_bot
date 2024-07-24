package com.danylenko.workhelper.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardServiceImpl implements KeyboardService {
    @Override
    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("largePrivatization-english"));
        keyboardRows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Перевірити к-сть обʼєктів"));
        keyboardRows.add(row2);

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Перевірити поточний баланс"));
        keyboardRows.add(row3);

        KeyboardRow row4 = new KeyboardRow();
        row4.add(new KeyboardButton("Витрати в поточному місяці"));
        keyboardRows.add(row4);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true); // робить клавіатуру адаптивною

        return replyKeyboardMarkup;
    }

}

package com.danylenko.workhelper.service.util;

import org.springframework.stereotype.Service;

@Service
public class BalanceFormat {
    public String formatBalance(int balance) {
        String balanceStr = String.valueOf(Math.abs(balance));
        StringBuilder formatted = new StringBuilder();
        int length = balanceStr.length();

        for (int i = 0; i < length; i++) {
            formatted.append(balanceStr.charAt(i));
            if (length == 8 && i == 2 || length == 7 && i == 1 || length == 6 && i == 0) {
                formatted.append(' ');
            } else if (length == 8 && i == 5 || length == 7 && i == 4 || length == 6 && i == 3 || length == 5 && i == 2 || length == 4 && i == 1 || length == 3 && i == 0) {
                formatted.append(',');
            }
        }

        if (balance < 0) {
            formatted.insert(0, '-');
        }

        return formatted.toString();
    }
}

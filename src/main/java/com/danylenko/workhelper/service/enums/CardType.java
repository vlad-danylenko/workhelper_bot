package com.danylenko.workhelper.service.enums;

public enum CardType {
    PLATINUM("platinum", "Платинова"),
    WHITE("white", "Біла"),
    EAID("eAid", "єПідтримка"),
    BLACK("black", "Чорна");

    private final String type;
    private final String displayName;
    CardType(String type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    public static String getDisplayName(String type) {
        for (CardType accountType : values()) {
            if (accountType.type.equals(type)) {
                return accountType.displayName;
            }
        }
        return type; // Повертає початковий тип, якщо відповідність не знайдена
    }

}

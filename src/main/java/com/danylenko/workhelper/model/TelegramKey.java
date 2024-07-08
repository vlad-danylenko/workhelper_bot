package com.danylenko.workhelper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "telegram_key")
public class TelegramKey {
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "api_key")
    private String apiKey;
}

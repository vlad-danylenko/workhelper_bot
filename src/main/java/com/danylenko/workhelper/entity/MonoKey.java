package com.danylenko.workhelper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "telegram_key")
public class MonoKey {
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "api_key")
    private String apiKey;
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private MonoClient monoClient;
}

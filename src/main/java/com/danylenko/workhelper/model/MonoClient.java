package com.danylenko.workhelper.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "telegram_client")
public class MonoClient {
    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "monoClient")
    private List<MonoKey> monoKeys;
}

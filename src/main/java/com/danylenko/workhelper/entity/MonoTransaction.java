package com.danylenko.workhelper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "mono_transaction")
public class MonoTransaction {
    @Id
    @Column(name = "operation_id")
    private String operationId;
    @Column(name = "client_id")
    private String monoClient;
    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;
    @Column(name = "operation_amount")
    private int operationAmount;


}

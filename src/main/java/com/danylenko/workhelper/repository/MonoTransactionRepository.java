package com.danylenko.workhelper.repository;

import com.danylenko.workhelper.entity.MonoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonoTransactionRepository extends JpaRepository<MonoTransaction,String> {
}

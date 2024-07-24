package com.danylenko.workhelper.repository;

import com.danylenko.workhelper.entity.MonoClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonoClientRepository extends JpaRepository<MonoClient,String> {
}

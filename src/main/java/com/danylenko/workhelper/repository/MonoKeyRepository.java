package com.danylenko.workhelper.repository;

import com.danylenko.workhelper.entity.MonoKey;
import org.springframework.data.repository.CrudRepository;

public interface MonoKeyRepository extends CrudRepository<MonoKey,String> {
    MonoKey findByUserId(String userId);
}

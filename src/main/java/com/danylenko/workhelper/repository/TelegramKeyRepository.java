package com.danylenko.workhelper.repository;

import com.danylenko.workhelper.model.TelegramKey;
import org.springframework.data.repository.CrudRepository;

public interface TelegramKeyRepository extends CrudRepository<TelegramKey,String> {
    TelegramKey findByUserId(String userId);
}

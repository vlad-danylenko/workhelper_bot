package com.danylenko.workhelper.repository;

import com.danylenko.workhelper.entity.ObjectCount;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ObjectCountRepository extends CrudRepository<ObjectCount,Long> {
    List<ObjectCount> findByHdayIn(List<String> dates);
}

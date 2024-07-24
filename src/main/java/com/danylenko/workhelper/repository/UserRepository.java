package com.danylenko.workhelper.repository;

import com.danylenko.workhelper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}

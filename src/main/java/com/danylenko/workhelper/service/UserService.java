package com.danylenko.workhelper.service;

import com.danylenko.workhelper.dto.CreateUserDto;
import com.danylenko.workhelper.dto.UserDto;
import com.danylenko.workhelper.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getById(Long userId);
    UserDto createUser(CreateUserDto createUserDto);
    UserDto updateUser(Long userId, CreateUserDto updateUserDto);
    void deleteUser(Long userId);

}

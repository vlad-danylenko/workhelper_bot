package com.danylenko.workhelper.service;

import com.danylenko.workhelper.dto.CreateAddressDto;
import com.danylenko.workhelper.dto.CreateUserDto;
import com.danylenko.workhelper.dto.UserDto;
import com.danylenko.workhelper.entity.Address;
import com.danylenko.workhelper.entity.Animal;
import com.danylenko.workhelper.entity.User;
import com.danylenko.workhelper.mapper.UserMapper;
import com.danylenko.workhelper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getById(Long userId) {
        return userRepository.findById(userId).map(userMapper::toDto).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {

        User user = userMapper.toModel(createUserDto);
        for (Address address : user.getAddresses()) {
            address.setUser(user);
        }
        Animal animal = new Animal();
        animal.setName(createUserDto.animal().name());
        animal.setUser(user);
        user.setAnimal(animal);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, CreateUserDto updateUserDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existingUser.setName(updateUserDto.name());
        existingUser.setEmail(updateUserDto.email());

        existingUser.getAddresses().clear();
        for (CreateAddressDto addressDto : updateUserDto.addresses()) {
            Address newAddress = new Address();
            newAddress.setStreet(addressDto.street());
            newAddress.setCity(addressDto.city());
            newAddress.setUser(existingUser);
            existingUser.getAddresses().add(newAddress);
        }

        Animal animal = existingUser.getAnimal();
        if (animal == null) {
            animal = new Animal();
            animal.setUser(existingUser);
            existingUser.setAnimal(animal);
        }
        animal.setName(updateUserDto.animal().name());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}

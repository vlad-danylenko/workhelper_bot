package com.danylenko.workhelper.contoller;

import com.danylenko.workhelper.dto.CreateUserDto;
import com.danylenko.workhelper.dto.UserDto;
import com.danylenko.workhelper.entity.User;
import com.danylenko.workhelper.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }

    @PutMapping("/{id}") // Новий метод
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserDto));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }


}

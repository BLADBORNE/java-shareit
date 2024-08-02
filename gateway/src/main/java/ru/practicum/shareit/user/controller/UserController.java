package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.constraint.CreatedUser;
import ru.practicum.shareit.user.constraint.UpdatedUser;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable(value = "userId", required = false) Integer userId) {
        return userClient.getUserById(userId);
    }

    @Validated(value = CreatedUser.class)
    @PostMapping
    public ResponseEntity<Object> createNewUser(@Valid @RequestBody User user) {
        return userClient.createNewUser(user);
    }

    @Validated(value = UpdatedUser.class)
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable(value = "userId") Integer userId,
            @Valid @RequestBody User user
    ) {
        return userClient.updateUser(userId, user);
    }

    @DeleteMapping(value = {"", "/{userId}"})
    public void deleteUserById(
            @PathVariable(value = "userId") Integer userId
    ) {
        userClient.deleteUserById(userId);
    }
}
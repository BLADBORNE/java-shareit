package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable(value = "userId", required = false) int userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User createNewUser(@Valid @RequestBody User user) {
        return userService.createNewUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable(value = "userId") int userId, @RequestBody User user) {
        return userService.updateUser(user, userId);
    }

    @DeleteMapping(value = {"", "/{userId}"})
    public User deleteUserById(
            @PathVariable(value = "userId", required = false) Optional<Integer> userId
    ) {
        if (userId.isEmpty()) {
            throw new IllegalArgumentException("При удалении пользователя не был передан id");
        }

        return userService.deleteUserById(userId.get());
    }
}
package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        List<UserDto> userList = userService.getUsers().stream().map(UserMapper::toUSerDto).collect(Collectors.toList());

        log.info("Все пользователи успешно отправлены клиенту");

        return ResponseEntity.ok().body(userList).getBody();
    }

    @GetMapping("/{userId}")
    public UserDto getUserDtoById(@PathVariable(value = "userId", required = false) Integer userId) {
        UserDto curUser = UserMapper.toUSerDto(userService.getUserById(userId));

        log.info("Пользователь с id = {} успешно отправлен клиенту", userId);

        return ResponseEntity.ok().body(curUser).getBody();
    }

    @PostMapping
    public UserDto createNewUser(@RequestBody User user) {
        UserDto createUser = UserMapper.toUSerDto(userService.createNewUser(user));

        log.info("Пользователь с id = {} успешно создан", createUser.getId());

        return ResponseEntity.ok().body(createUser).getBody();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable(value = "userId") Integer userId, @RequestBody User user) {
        UserDto updatedUser = UserMapper.toUSerDto(userService.updateUser(user, userId));

        log.info("Пользователь с id = {} успешно обновлен", userId);

        return ResponseEntity.ok().body(updatedUser).getBody();
    }

    @DeleteMapping(value = {"", "/{userId}"})
    public void deleteUserById(
            @PathVariable(value = "userId") Integer userId
    ) {
        userService.deleteUserById(userId);

        log.info("Пользователь с id = {} был успешно удален", userId);

        ResponseEntity.ok();
    }
}
package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.AlreadyExistException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDao implements UserStorage {
    private final UserMapper userMapper;
    private Map<Integer, User> users = new HashMap<>();
    private Map<Integer, String> usersEmails = new HashMap<>();
    private int generatedUserId = 0;

    @Override
    public UserDto createNewUser(User user) {
        log.info("Получен запрос на создание пользователя");

        checkDuplicateEmailWhenCreateUser(user.getEmail());

        user.setId(++generatedUserId);

        users.put(generatedUserId, user);
        usersEmails.put(generatedUserId, user.getEmail());

        log.info("Полтзователь с id = {} успешно создан", user.getId());

        return userMapper.toUSerDto(user);
    }

    @Override
    public UserDto updateUser(User user, int userId) {
        log.info("Получен запрос на обновление пользовтаеля с id = {}", userId);

        User curUser = getUserById(userId);

        getUserAndCheckDuplicateEmailWhenUpdateUser(curUser, user);

        if (user.getName() != null) {
            curUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            curUser.setEmail(user.getEmail());
        }

        usersEmails.put(userId, user.getEmail());

        log.info("Пользователь с id = {} успешно обновлен", userId);

        return userMapper.toUSerDto(curUser);
    }

    @Override
    public UserDto getUserDtoById(int userId) {
        User user = getUserById(userId);

        return userMapper.toUSerDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return users.values().stream().map(userMapper::toUSerDto).collect(Collectors.toList());
    }

    @Override
    public UserDto deleteUserById(int userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);

        User deletedUser = getUserById(userId);

        users.remove(userId);
        usersEmails.remove(userId);

        log.info("Пользователь с id = {} был успешно удален", deletedUser.getId());

        return userMapper.toUSerDto(deletedUser);
    }

    private void checkDuplicateEmailWhenCreateUser(String email) {
        if (usersEmails.containsValue(email)) {
            log.warn("Пользователь пытается использовать уже занятый email");

            throw new AlreadyExistException(String.format("%s уже используется, выберите другой", email));
        }
    }

    public User getUserById(int userId) {
        log.info("Получен запрос на отправку пользователя с id = {}", userId);

        if (users.containsKey(userId)) {
            log.info("Пользователь с id = {} успешно отправлен клиенту", userId);

            return users.get(userId);
        }

        log.warn("Отсутствует пользователь с id = {}", userId);

        throw new NoSuchElementException(String.format("Пользователь с id = %s отсутствует", userId));
    }

    public void getUserAndCheckDuplicateEmailWhenUpdateUser(User curUser, User user) {
        if (user.getEmail() == null) {
            return;
        }

        if (curUser.getEmail().equals(user.getEmail())) {
            return;
        }

        checkDuplicateEmailWhenCreateUser(user.getEmail());
    }
}
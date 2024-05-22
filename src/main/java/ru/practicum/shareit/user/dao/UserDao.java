package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.AlreadyExistException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class UserDao implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private Map<Integer, String> usersEmails = new HashMap<>();
    private int generatedUserId = 0;

    @Override
    public User createNewUser(User user) {
        log.info("Получен запрос на создание пользовтаеля");

        checkDuplicateEmailWhenCreateUser(user.getEmail());

        user.setId(++generatedUserId);

        users.put(generatedUserId, user);
        usersEmails.put(generatedUserId, user.getEmail());

        log.info("Полтзователь с id = {} успешно создан", user.getId());

        return user;
    }

    @Override
    public User updateUser(User user, int userId) {
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

        return curUser;
    }

    @Override
    public User getUserById(int userId) {
        log.info("Получен запрос на отправку пользователя с id = {}", userId);

        if (users.containsKey(userId)) {
            log.info("Пользователь с id = {} успешно отправлен клиенту", userId);

            return users.get(userId);
        }

        log.warn("Отсутствует пользователь с id = {}", userId);

        throw new NoSuchElementException(String.format("Пользователь с id = %s отсутствует", userId));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User deleteUserById(int userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);

        User deletedUser = getUserById(userId);

        users.remove(userId);
        usersEmails.remove(userId);

        log.info("Пользователь с id = {} был успешно удален", deletedUser.getId());

        return deletedUser;
    }

    private void checkDuplicateEmailWhenCreateUser(String email) {
        if (usersEmails.containsValue(email)) {
            log.warn("Пользователь пытается использовать уже занятый email");

            throw new AlreadyExistException(String.format("%s уже используется, выберите другой", email));
        }
    }

    private void getUserAndCheckDuplicateEmailWhenUpdateUser(User curUser, User user) {
        if (user.getEmail() == null) {
            return;
        }

        if (curUser.getEmail().equals(user.getEmail())) {
            return;
        }

        checkDuplicateEmailWhenCreateUser(user.getEmail());
    }
}
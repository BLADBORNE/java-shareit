package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createNewUser(User user) {
        return userStorage.createNewUser(user);
    }

    public User updateUser(User user, int userId) {
        return userStorage.updateUser(user, userId);
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User deleteUserById(int userId) {
        return userStorage.deleteUserById(userId);
    }
}
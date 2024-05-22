package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User createNewUser(User user);

    User updateUser(User user, int userId);

    User getUserById(int userId);

    List<User> getUsers();

    User deleteUserById(int userId);
}
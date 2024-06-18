package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createNewUser(User user);

    User updateUser(User user, int userId);

    List<User> getUsers();

    User getUserById(int id);

    void deleteUserById(int userId);
}
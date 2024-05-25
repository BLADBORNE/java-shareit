package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    UserDto createNewUser(User user);

    UserDto updateUser(User user, int userId);

    UserDto getUserDtoById(int userId);

    User getUserById(int userId);

    List<UserDto> getUsers();

    UserDto deleteUserById(int userId);
}
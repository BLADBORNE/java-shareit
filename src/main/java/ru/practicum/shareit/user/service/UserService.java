package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto createNewUser(User user) {
        return userStorage.createNewUser(user);
    }

    public UserDto updateUser(User user, int userId) {
        return userStorage.updateUser(user, userId);
    }

    public UserDto getUserDtoById(int userId) {
        return userStorage.getUserDtoById(userId);
    }

    public List<UserDto> getUsersDto() {
        return userStorage.getUsers();
    }

    public UserDto deleteUserById(int userId) {
        return userStorage.deleteUserById(userId);
    }
}
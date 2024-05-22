package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    private UserMapper() {
    }

    public static UserDto toUSerDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUSerFromDto(UserDto user, int userId) {
        return new User(
                userId,
                user.getName() != null ? user.getName() : null,
                user.getEmail() != null ? user.getEmail() : null
        );
    }
}
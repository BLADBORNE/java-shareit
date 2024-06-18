package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.exception.AlreadyExistException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class DuplicateFinder {
    private final UserRepository userRepository;

    public void checkDuplicateEmailsWhenCreate(String email) {
        List<User> emailsRepeat = userRepository.findByEmailEquals(email);

        if (!emailsRepeat.isEmpty()) {
            log.warn("Пользователь пытается использовать уже занятый email");

            throw new AlreadyExistException(String.format("%s уже используется, выберите другой", email));
        }
    }

    public void checkDuplicateEmailsWhenUpdate(User curUser, User updatedUser) {
        if (updatedUser.getEmail() == null) {
            return;
        }

        if (curUser.getEmail().equals(updatedUser.getEmail())) {
            return;
        }

        checkDuplicateEmailsWhenCreate(updatedUser.getEmail());
    }
}
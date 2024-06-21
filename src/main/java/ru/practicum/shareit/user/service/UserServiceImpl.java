package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DuplicateFinder duplicateFinder;

    @Override
    public User createNewUser(User user) {
        log.info("Получен запрос на создание пользователя");

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User updatedUser, int userId) {
        log.info("Получен запрос на обновление пользовтаеля с id = {}", userId);

        User curUser = getUserById(userId);

        duplicateFinder.checkDuplicateEmailsWhenUpdate(curUser, updatedUser);

        if (updatedUser.getName() != null && !curUser.getName().equals(updatedUser.getName())) {
            curUser.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null && !curUser.getEmail().equals(updatedUser.getEmail())) {
            curUser.setEmail(updatedUser.getEmail());
        }

        userRepository.save(curUser);

        return curUser;
    }

    @Override
    public User getUserById(int userId) {
        log.info("Получен запрос на отправку пользователя с id = {}", userId);

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return user.get();
        }

        log.info("Пользователь с id = {} не найден", userId);

        throw new NoSuchElementException("Пользователь не найден");
    }

    @Override
    public List<User> getUsers() {
        log.info("Получен запрос на отправку всех пользователей");

        return userRepository.findAll();
    }

    @Override
    public void deleteUserById(int userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);

        getUserById(userId);

        userRepository.deleteById(userId);
    }
}
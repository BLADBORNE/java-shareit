package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.exception.AlreadyExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.DuplicateFinder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DuplicateFinderTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private DuplicateFinder duplicateFinder;
    private User user;

    @BeforeEach
    public void createUser() {
        user = User.builder()
                .id(1)
                .name("Ilya")
                .email("belyachok567811@gmail.com")
                .build();
    }

    @Test
    public void shouldGetValidEmail() {
        User updatedUser = User.builder()
                .id(23)
                .name("Maxim")
                .email("iliasacool@gmail.com")
                .build();

        when(userRepository.findByEmailEquals(anyString())).thenReturn(Optional.empty());

        duplicateFinder.checkDuplicateEmailsWhenUpdate(user, updatedUser);
    }

    @Test
    public void shouldThrownExceptionIfUpdatedEmailAlreadyExists() {
        User updatedUser = User.builder()
                .id(23)
                .name("Maxim")
                .email("iliasacool@gmail.com")
                .build();

        when(userRepository.findByEmailEquals(anyString())).thenReturn(Optional.of(user));

        assertThrows(AlreadyExistException.class,
                () -> duplicateFinder.checkDuplicateEmailsWhenUpdate(user, updatedUser));
    }
}

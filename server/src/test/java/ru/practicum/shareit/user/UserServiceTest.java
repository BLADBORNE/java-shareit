package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.exception.AlreadyExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.DuplicateFinder;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private DuplicateFinder duplicateFinder;
    private User user;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    public void createUser() {
        user = User.builder()
                .id(1)
                .name("Ilya")
                .email("belyachok567811@gmail.com")
                .build();
    }

    @Test
    public void shouldCreateNewUser() {
        when(userRepository.save(any())).thenReturn(user);

        User expected = userService.createNewUser(user);

        assertThat(expected, is(notNullValue()));
        assertThat(expected, is(user));
    }

    @Test
    public void shouldThrowExceptionIfUserDoesntExistsInRepository() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        NoSuchElementException elementException = assertThrows(NoSuchElementException.class,
                () -> userService.getUserById(1));

        assertThat(elementException.getMessage(), is("Пользователь не найден"));
    }

    @Test
    public void shouldReturnUserIfUserExistsInRepository() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User expected = userService.getUserById(1);

        assertThat(expected, is(notNullValue()));
        assertThat(user, is(expected));
    }

    @Test
    public void shouldThrowExceptionIfUpdatedEmailsAlreadyExistsInRepository() {
        User updatedUser = User.builder()
                .id(23)
                .name("Maxim")
                .email("iliasacool@gmail.com")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        doThrow(AlreadyExistException.class).when(duplicateFinder).checkDuplicateEmailsWhenUpdate(any(), any());

        assertThrows(AlreadyExistException.class,
                () -> userService.updateUser(updatedUser, 1));

        verify(userRepository, never()).save(updatedUser);
    }

    @Test
    public void shouldUpdatedNewEmailAndNewNameIfUpdatedEmailsDoesntExistsInRepository() {
        User updatedUser = User.builder()
                .id(23)
                .name("Maxim")
                .email("iliasacool@gmail.com")
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.updateUser(updatedUser, 1);

        verify(userRepository, times(1)).save(any());
        verify(userRepository).save(userCaptor.capture());

        User getUpdatedUser = userCaptor.getValue();

        assertThat(getUpdatedUser, is(notNullValue()));
        assertThat(getUpdatedUser.getId(), is(1));
        assertThat(updatedUser.getName(), is(getUpdatedUser.getName()));
        assertThat(updatedUser.getEmail(), is(getUpdatedUser.getEmail()));
    }

    @Test
    public void shouldDeleteUserIfUserExistsInRepository() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUserById(1);
    }

    @Test
    public void shouldThrowExceptionIfUserDoesntExistInRepository() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.deleteUserById(1));
    }

    @Test
    public void shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getUsers();

        assertThat(users, is(notNullValue()));
        assertThat(users.size(), is(1));
        assertThat(user, is(users.get(0)));
    }
}
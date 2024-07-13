package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.AlreadyExistException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void createUser() {
        user = User.builder()
                .id(1)
                .name("Ilya")
                .email("belyachok567811@gmail.com")
                .build();

        userDto = UserMapper.toUSerDto(user);
    }

    @Test
    public void shouldCreateUser() throws Exception {
        when(userService.createNewUser(any())).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionWhenCreatedUserNameIsNotValid() throws Exception {
        user.setName("");

        ResultActions resultActions = mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserValidationException(resultActions);
    }

    @Test
    public void shouldThrownExceptionWhenCreatedUserEmailIsNotValid() throws Exception {
        user.setEmail("email");

        ResultActions resultActions = mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserValidationException(resultActions);
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        User updatedUser = User.builder()
                .id(1)
                .name("Maxim")
                .email("iliasacool@gmail.com")
                .build();

        userDto = UserMapper.toUSerDto(updatedUser);

        when(userService.updateUser(any(), anyInt())).thenReturn(updatedUser);

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}", user.getId())
                .content(objectMapper.writeValueAsString(updatedUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUpdatedEmailIsNotValid() throws Exception {
        User updatedUser = User.builder()
                .id(1)
                .name("Maxim")
                .email("email")
                .build();

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}", user.getId())
                .content(objectMapper.writeValueAsString(updatedUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserValidationException(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUpdatedEmailAlreadyExistsWhenUpdateUSer() throws Exception {
        User updatedUser = User.builder()
                .id(1)
                .name("Maxim")
                .email("iliasacool@gmail.com")
                .build();

        when(userService.updateUser(any(), anyInt())).thenThrow(AlreadyExistException.class);

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}", user.getId())
                .content(objectMapper.writeValueAsString(updatedUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserAlreadyExistsException(resultActions);
    }

    @Test
    public void shouldThrownThrowableWhenUpdatedUSer() throws Exception {
        User updatedUser = User.builder()
                .id(1)
                .name("Maxim")
                .email("iliasacool@gmail.com")
                .build();

        when(userService.updateUser(any(), anyInt())).thenThrow(DataIntegrityViolationException.class);

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}", user.getId())
                .content(objectMapper.writeValueAsString(updatedUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserThrowable(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUpdatedNamesNotValid() throws Exception {
        User updatedUser = User.builder()
                .id(1)
                .name(" ")
                .email("iliasacool@gmail.com")
                .build();

        ResultActions resultActions = mockMvc.perform(patch("/users/{userId}", user.getId())
                .content(objectMapper.writeValueAsString(updatedUser))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserValidationException(resultActions);
    }

    @Test
    public void shouldGetUserByIdIfUserExistsInRepository() throws Exception {
        when(userService.getUserById(anyInt())).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(get("/users/{userId}", user.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsInRepository() throws Exception {
        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(get("/users/{userId}", user.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserNotFoundException(resultActions);
    }

    @Test
    public void shouldDeleteUserByIdWhenUserExistsInRepository() throws Exception {
        doNothing().when(userService);

        ResultActions resultActions = mockMvc.perform(delete("/users/{userId}", user.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkStatusIsOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionWhenUserDoesntExistsInRepository() throws Exception {
        doThrow(NoSuchElementException.class).when(userService).deleteUserById(anyInt());

        ResultActions resultActions = mockMvc.perform(delete("/users/{userId}", user.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserNotFoundException(resultActions);
    }

    @Test
    public void shouldGetUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(user));

        ResultActions resultActions = mockMvc.perform(get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkUserListOk(resultActions);
    }

    private void checkUserOk(ResultActions request) throws Exception {
        request
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((userDto))));
    }

    private void checkUserListOk(ResultActions request) throws Exception {
        request
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto))));
    }

    private void checkUserNotFoundException(ResultActions request) throws Exception {
        request
                .andExpect(status().is(404));
    }

    private void checkUserAlreadyExistsException(ResultActions request) throws Exception {
        request
                .andExpect(status().is(409));
    }

    private void checkUserThrowable(ResultActions request) throws Exception {
        request
                .andExpect(status().is(500));
    }

    private void checkUserValidationException(ResultActions request) throws Exception {
        request
                .andExpect(status().is(400));
    }

    private void checkStatusIsOk(ResultActions request) throws Exception {
        request
                .andExpect(status().isOk());
    }
}
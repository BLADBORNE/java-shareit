package ru.practicum.shareit.ItemRequest;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestCreationDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private final String headerUserId = "X-Sharer-User-Id";

    @BeforeEach
    public void create() {
        user = User.builder()
                .id(1)
                .name("Ilya")
                .email("belyachok567811@gmail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1)
                .description("Some Description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Test
    public void shouldCreateValidItemRequest() throws Exception {
        ResponseItemRequestCreationDto creationDto = ItemRequestMapper.responseItemRequestCreationDto(itemRequest);

        when(itemRequestService.addItemRequest(any(), anyInt())).thenReturn(itemRequest);

        ResultActions resultActions = mockMvc.perform(post("/requests")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkCreateItemRequestOk(resultActions, creationDto);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenCreateValidItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(any(), anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(post("/requests")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestNotFoundCheck(resultActions);
    }

    @Test
    public void shouldThrowExceptionIfRequestHeaderDoesntExistsWhenCreateUser() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/requests")
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestBadRequestCheck(resultActions);
    }

    @Test
    public void shouldThrowExceptionIfCreatedItemRequestIsNotValid() throws Exception {
        itemRequest.setDescription(null);

        when(itemRequestService.addItemRequest(any(), anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(post("/requests")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestBadRequestCheck(resultActions);
    }

    @Test
    public void shouldThrownServerError() throws Exception {
        when(itemRequestService.addItemRequest(any(), anyInt())).thenThrow(DataIntegrityViolationException.class);

        ResultActions resultActions = mockMvc.perform(post("/requests")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestServerErrorCheck(resultActions);
    }

    @Test
    public void shouldGetUserItemRequests() throws Exception {
        when(itemRequestService.getUserItemRequests(anyInt())).thenReturn(List.of(itemRequest));

        ResultActions resultActions = mockMvc.perform(get("/requests")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestListOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetUserItemRequests() throws Exception {
        when(itemRequestService.getUserItemRequests(anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(get("/requests")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestNotFoundCheck(resultActions);
    }

    @Test
    public void shouldGetAllItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequest));

        ResultActions resultActions = mockMvc.perform(get("/requests/all")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestListOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetAllItemRequests() throws Exception {
        when(itemRequestService.getAllItemRequests(anyInt(), anyInt(), anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(get("/requests/all")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestNotFoundCheck(resultActions);
    }

    @Test
    public void shouldGetItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyInt(), anyInt())).thenReturn(itemRequest);

        ResultActions resultActions = mockMvc.perform(get("/requests/{itemRequestId}", itemRequest.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemRequestOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetItemRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyInt(), anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(get("/requests/{itemRequestId}", itemRequest.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(itemRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        itemRequestNotFoundCheck(resultActions);
    }

    private void checkCreateItemRequestOk(ResultActions resultActions, ResponseItemRequestCreationDto creationDto)
            throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((creationDto))));
    }

    private void itemRequestNotFoundCheck(ResultActions resultActions)
            throws Exception {
        resultActions
                .andExpect(status().is(404));
    }

    private void itemRequestBadRequestCheck(ResultActions resultActions)
            throws Exception {
        resultActions
                .andExpect(status().is(400));
    }

    private void itemRequestServerErrorCheck(ResultActions resultActions)
            throws Exception {
        resultActions
                .andExpect(status().is(500));
    }

    private void checkItemRequestOk(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((itemRequestDto))));
    }

    private void itemRequestListOk(ResultActions resultActions)
            throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((List.of(itemRequestDto)))));
    }
}
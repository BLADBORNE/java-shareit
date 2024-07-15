package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.exception.PermissionException;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;
    private Item item;
    private User user;
    private ItemDto itemDto;
    private final String headerUserId = "X-Sharer-User-Id";

    @BeforeEach
    public void create() {
        user = User.builder()
                .id(1)
                .name("Ilya")
                .email("belyachok567811@gmail.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("Дрель")
                .description("Красивая дрель")
                .available(true)
                .owner(user)
                .build();

        itemDto = ItemMapper.toItemDto(item);
    }

    @Test
    public void shouldCreateValidItem() throws Exception {
        when(itemService.createNewItem(any(), anyInt())).thenReturn(item);

        ResultActions resultActions = mockMvc.perform(post("/items")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfCreatedUserNameIsNotValid() throws Exception {
        item.setName(" ");

        ResultActions resultActions = mockMvc.perform(post("/items")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkFailedHeaderAndItemFieldsValidationAndBedRequest(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenCreatedUser() throws Exception {
        when(itemService.createNewItem(any(), anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(post("/items")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemNotFound(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfHeaderDoesntExists() throws Exception {
        when(itemService.createNewItem(any(), anyInt())).thenReturn(item);

        ResultActions resultActions = mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkFailedHeaderAndItemFieldsValidationAndBedRequest(resultActions);
    }

    @Test
    public void shouldUpdateItemWithValidFields() throws Exception {
        Item updatedItem = Item.builder()
                .id(1)
                .name("Дрель23")
                .description("Очень красивая дрель")
                .available(false)
                .owner(user)
                .build();

        itemDto = ItemMapper.toItemDto(updatedItem);

        when(itemService.updateItem(any(), anyInt(), anyInt())).thenReturn(updatedItem);

        ResultActions resultActions = mockMvc.perform(patch("/items/{itemId}", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfPermissionWasDeniedWhenUpdatedItem() throws Exception {
        when(itemService.updateItem(any(), anyInt(), anyInt())).thenThrow(PermissionException.class);

        ResultActions resultActions = mockMvc.perform(patch("/items/{itemId}", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkFailedHeaderAndItemFieldsValidationAndBedRequest(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUpdatedItemDescriptionIsNotValid() throws Exception {
        item.setDescription("");

        ResultActions resultActions = mockMvc.perform(patch("/items/{itemId}", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkFailedHeaderAndItemFieldsValidationAndBedRequest(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserOrItemDoesntExistsWhenUpdateItem() throws Exception {
        when(itemService.updateItem(any(), anyInt(), anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(patch("/items/{itemId}", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemNotFound(resultActions);
    }

    @Test
    public void shouldGetItemById() throws Exception {
        when(itemService.getItemById(anyInt(), anyInt())).thenReturn(item);

        ResultActions resultActions = mockMvc.perform(get("/items/{itemId}", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfItemOrUserDoesntExistsWhenGetItemById() throws Exception {
        when(itemService.getItemById(anyInt(), anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(get("/items/{itemId}", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemNotFound(resultActions);
    }

    @Test
    public void shouldGetUsersItems() throws Exception {
        when(itemService.getUsersItems(anyInt())).thenReturn(List.of(item));

        ResultActions resultActions = mockMvc.perform(get("/items")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemListOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetUsersItems() throws Exception {
        when(itemService.getUsersItems(anyInt())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(get("/items")
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemNotFound(resultActions);
    }

    @Test
    public void shouldGetItemsForSearch() throws Exception {
        when(itemService.getItemsForSearch(anyInt(), anyString())).thenReturn(List.of(item));

        ResultActions resultActions = mockMvc.perform(get("/items/search")
                .header(headerUserId, user.getId())
                .param("text", item.getDescription())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemListOk(resultActions);
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetItemsForSearch() throws Exception {
        when(itemService.getItemsForSearch(anyInt(), anyString())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(get("/items/search")
                .header(headerUserId, user.getId())
                .param("text", item.getDescription())
                .content(objectMapper.writeValueAsString(item))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemNotFound(resultActions);
    }

    @Test
    public void shouldAddCommentToItem() throws Exception {
        Comment comment = Comment.builder()
                .id(1)
                .item(item)
                .author(user)
                .text("Some text")
                .created(LocalDateTime.now())
                .build();

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        when(commentService.addCommentToItem(anyInt(), anyInt(), any())).thenReturn(comment);

        ResultActions resultActions = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(comment))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkCommentOk(resultActions, commentDto);
    }

    @Test
    public void shouldThrownExceptionIfItemOrUserDoesntExistsWhenAddCommentToItem() throws Exception {
        Comment comment = Comment.builder()
                .id(1)
                .item(item)
                .author(user)
                .text("Some text")
                .created(LocalDateTime.now())
                .build();

        when(commentService.addCommentToItem(anyInt(), anyInt(), any())).thenThrow(NoSuchElementException.class);

        ResultActions resultActions = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(comment))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkItemNotFound(resultActions);
    }

    @Test
    public void shouldThrownServerErrorExceptionWhenAddCommentToItem() throws Exception {
        Comment comment = Comment.builder()
                .id(1)
                .item(item)
                .author(user)
                .text("Some text")
                .created(LocalDateTime.now())
                .build();

        when(commentService.addCommentToItem(anyInt(), anyInt(), any())).thenThrow(DataIntegrityViolationException.class);

        ResultActions resultActions = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                .header(headerUserId, user.getId())
                .content(objectMapper.writeValueAsString(comment))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        checkThrown(resultActions);
    }

    private void checkItemOk(ResultActions request) throws Exception {
        request
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((itemDto))));
    }

    private void checkFailedHeaderAndItemFieldsValidationAndBedRequest(ResultActions request) throws Exception {
        request
                .andExpect(status().is(400));
    }

    private void checkItemNotFound(ResultActions request) throws Exception {
        request
                .andExpect(status().is(404));
    }

    private void checkItemListOk(ResultActions request) throws Exception {
        request
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((List.of(itemDto)))));
    }

    private void checkCommentOk(ResultActions request, CommentDto commentDto) throws Exception {
        request
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString((commentDto))));
    }

    private void checkThrown(ResultActions request) throws Exception {
        request
                .andExpect(status().is(500));
    }
}
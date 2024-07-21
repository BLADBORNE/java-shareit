package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.ItemBookingService;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.exception.PermissionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemBookingService bookingService;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private User user;
    private ItemCreationDto creationDto;
    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @BeforeEach
    public void createItem() {
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
    }

    @Test
    public void shouldCreateItemWithValidFields() {
        creationDto = ItemCreationDto.builder()
                .name("Дрель")
                .description("Красивая дрель")
                .build();

        when(itemRepository.save(any())).thenReturn(item);

        Item createditem = itemService.createNewItem(creationDto, anyInt());

        assertThat(createditem, is(item));
    }

    @Test
    public void shouldCreateItemWithValidFieldsAndItemRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1)
                .description("dede")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        item.setRequest(itemRequest);

        creationDto = ItemCreationDto.builder()
                .name("Дрель")
                .description("Красивая дрель")
                .requestId(1)
                .build();

        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));

        when(itemRepository.save(any())).thenReturn(item);

        Item createditem = itemService.createNewItem(creationDto, anyInt());

        assertThat(createditem, is(item));
    }

    @Test
    public void shouldThrowExceptionWhenItemOwnerDoesntExists() {
        creationDto = ItemCreationDto.builder()
                .name("Дрель")
                .description("Красивая дрель")
                .build();

        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class,
                () -> itemService.createNewItem(creationDto, anyInt()));
    }

    @Test
    public void shouldUpdateItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        Item updatedItem = Item.builder()
                .id(23)
                .name("Дрель23")
                .description("Очень красивая дрель")
                .available(false)
                .owner(user)
                .build();

        itemService.updateItem(updatedItem, 1, 1);

        verify(itemRepository, times(1)).save(any());
        verify(itemRepository).save(itemCaptor.capture());

        Item getUpdatedItem = itemCaptor.getValue();

        assertThat(getUpdatedItem.getId(), is(1));
        assertThat(getUpdatedItem.getName(), is(updatedItem.getName()));
        assertThat(getUpdatedItem.getDescription(), is(updatedItem.getDescription()));
        assertThat(getUpdatedItem.getAvailable(), is(false));
    }

    @Test
    public void shouldThrownPermissionExceptionWhenUpdateItem() {
        Item updatedItem = Item.builder()
                .id(23)
                .name("Дрель23")
                .description("Очень красивая дрель")
                .available(false)
                .owner(user)
                .build();

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(PermissionException.class, () -> itemService.updateItem(updatedItem, 1, anyInt()));
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenUpdatedItem() {
        Item updatedItem = Item.builder()
                .id(1)
                .name("Дрель")
                .description("Очень красивая дрель")
                .available(false)
                .owner(user)
                .build();

        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> itemService.updateItem(updatedItem, 1, 1));
    }

    @Test
    public void shouldThrownExceptionWhenUpdatedItemDoesntExists() {
        Item updatedItem = Item.builder()
                .id(1)
                .name("Дрель")
                .description("Очень красивая дрель")
                .available(false)
                .owner(user)
                .build();

        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.updateItem(updatedItem, 1, 1));
    }

    @Test
    public void shouldGetItemById() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        Item getItem = itemService.getItemById(1, 1);

        assertThat(getItem, is(item));
    }

    @Test
    public void shouldGetItemByIdForBookingAndComment() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        Item getItem = itemService.getItemByIdForBookingAndComment(anyInt());

        assertThat(getItem, is(item));
    }

    @Test
    public void shouldThrownExceptionIfItemDoesntExistsWhenItemByIdForBookingAndComment() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.getItemByIdForBookingAndComment(anyInt()));
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExists() {
        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> itemService.getItemById(1, 1));
    }

    @Test
    public void shouldThrownExceptionWhenItemDoesntExists() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemService.getItemById(1, 1));
    }

    @Test
    public void shouldGetUsersItems() {
        when(itemRepository.findByOwnerId(anyInt())).thenReturn(List.of(item));

        List<Item> usersItems = itemService.getUsersItems(anyInt());

        assertThat(item, is(usersItems.get(0)));
    }

    @Test
    public void shouldGetItemsForSearch() {
        when(itemRepository.getUsersItemsForSearch(anyString())).thenReturn(List.of(item));

        List<Item> items = itemService.getItemsForSearch(anyInt(), "anyString()");

        assertThat(item, is(items.get(0)));
    }

    @Test
    public void shouldGetEmptyItemsForSearchWhenSearchIsNull() {
        List<Item> a = itemService.getItemsForSearch(anyInt(), null);
        assertTrue(a.isEmpty());
    }

    @Test
    public void shouldGetEmptyItemsForSearchWhenSearchIsBlank() {
        List<Item> a = itemService.getItemsForSearch(anyInt(), "  ");
        assertTrue(a.isEmpty());
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWhenGetEmptyItemsForSearch() {
        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> itemService.getItemsForSearch(anyInt(), null));
    }

    @Test
    public void shouldFindByRequestIdIn() {
        when(itemRepository.findByRequestIdIn(anyList())).thenReturn(List.of(item));

        List<Item> items = itemService.findByRequestIdIn(anyList());

        assertThat(item, is(items.get(0)));
    }
}
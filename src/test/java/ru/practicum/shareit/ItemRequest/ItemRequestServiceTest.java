package ru.practicum.shareit.ItemRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequest itemRequest;

    @BeforeEach
    public void createItemRequest() {
        User user = User.builder()
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
    }

    @Test
    public void shouldAddItemRequest() {
        ItemRequestCreationDto creationDto = ItemRequestCreationDto.builder()
                .description("Some Description")
                .build();

        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequest getItemRequest = itemRequestService.addItemRequest(creationDto, anyInt());

        assertThat(itemRequest, is(getItemRequest));
    }

    @Test
    public void shouldThrownExceptionIfUserDoesNotExistsWhenCreatedItemRequest() {
        ItemRequestCreationDto creationDto = ItemRequestCreationDto.builder()
                .description("Some Description")
                .build();

        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> itemRequestService.addItemRequest(creationDto, anyInt()));
    }

    @Test
    public void shouldGetUserItemRequests() {
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyInt())).thenReturn(List.of(itemRequest));

        List<ItemRequest> itemRequests = itemRequestService.getUserItemRequests(anyInt());

        assertThat(itemRequest, is(itemRequests.get(0)));

    }

    @Test
    public void shouldThrownExceptionIfUserDoesNotExistsWhenGetUserItemRequests() {
        when(userService.getUserById((anyInt()))).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> itemRequestService.getUserItemRequests(anyInt()));
    }

    @Test
    public void shouldGetItemRequestById() {
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));

        ItemRequest getItemRequest = itemRequestService.getItemRequestById(1, anyInt());

        assertThat(itemRequest, is(getItemRequest));
    }

    @Test
    public void shouldThrowExceptionIfItemDoesNotExistsWhenGetItemRequestById() {
        when(itemRequestRepository.findById((anyInt()))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> itemRequestService.getItemRequestById(1, anyInt()));
    }

    @Test
    public void shouldThrowExceptionIfUserDoesNotExistsWhenGetItemRequestById() {
        when(userService.getUserById((anyInt()))).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> itemRequestService.getItemRequestById(1, anyInt()));
    }

    @Test
    public void shouldFindAllItemRequests() {
        when(itemRequestRepository.findByRequestorIdNot(anyInt(), any())).thenReturn(new PageImpl<>(List.of(itemRequest)));


        List<ItemRequest> itemRequests = itemRequestService.getAllItemRequests(1, anyInt(), 1);

        assertThat(itemRequest, is(itemRequests.get(0)));
    }

    @Test
    public void shouldThrowExceptionIfUserDoesNotExistsWhenGetAllItemRequests() {
        when(userService.getUserById((anyInt()))).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class, () -> itemRequestService.getUserItemRequests(anyInt()));
    }
}

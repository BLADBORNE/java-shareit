package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.exception.CommentNotAllowedException;
import ru.practicum.shareit.booking.service.UserBookingService;
import ru.practicum.shareit.comment.dto.CreationCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)

public class CommentServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserBookingService userBookingService;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentServiceImpl commentService;
    private Comment comment;
    private Item item;
    private User user;

    @BeforeEach
    public void createComment() {
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

        comment = Comment.builder()
                .id(1)
                .item(item)
                .author(user)
                .text("Test text")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void shouldCreateCommentToItem() {
        CreationCommentDto creationCommentDto = CreationCommentDto.builder()
                .text("Test text")
                .build();

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        when(commentRepository.save(any())).thenReturn(comment);

        Comment serviceComment = commentService.addCommentToItem(1, 1, creationCommentDto);

        assertThat(comment, is(serviceComment));
    }

    @Test
    public void shouldThrownExceptionIfItemDoesntExistsWheAddCommentToItem() {
        CreationCommentDto creationCommentDto = CreationCommentDto.builder()
                .text("Test text")
                .build();

        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> commentService.addCommentToItem(1, 1, creationCommentDto));
    }

    @Test
    public void shouldThrownExceptionIfCommentCheckIsNotValidDoesntExistsWheAddCommentToItem() {
        CreationCommentDto creationCommentDto = CreationCommentDto.builder()
                .text("Test text")
                .build();

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        doThrow(CommentNotAllowedException.class).when(userBookingService).commentCheck(anyInt(), anyInt());

        assertThrows(CommentNotAllowedException.class,
                () -> commentService.addCommentToItem(1, 1, creationCommentDto));
    }

    @Test
    public void shouldThrownExceptionIfUserDoesntExistsWheAddCommentToItem() {
        CreationCommentDto creationCommentDto = CreationCommentDto.builder()
                .text("Test text")
                .build();

        when(userService.getUserById(anyInt())).thenThrow(NoSuchElementException.class);

        assertThrows(NoSuchElementException.class,
                () -> commentService.addCommentToItem(1, 1, creationCommentDto));
    }

    @Test
    public void shouldGetItemComments() {
        when(commentRepository.getCommentByItemId(anyInt())).thenReturn(List.of(comment));

        List<Comment> itemsComments = commentService.getItemComments(anyInt());

        assertThat(comment, is(itemsComments.get(0)));
    }
}

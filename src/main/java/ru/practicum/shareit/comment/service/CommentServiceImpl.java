package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.service.UserBookingService;
import ru.practicum.shareit.comment.dto.CreationCommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserBookingService userBookingService;
    private final CommentRepository commentRepository;

    @Override
    public Comment addCommentToItem(int userId, int itemId, CreationCommentDto dto) {
        log.info("Получен запрос на добавление отзыва от пользователя с id = {} вещи с id = {}", userId, itemId);

        User user = userService.getUserById(userId);

        log.info("Получен запрос на получение вещи с id = {}", itemId);

        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isPresent()) {
            userBookingService.commentCheck(userId, itemId);

            return commentRepository.save(CommentMapper.toComment(dto, user, item.get()));
        }

        log.warn("Отсутствует вещь с id = {}", itemId);

        throw new NoSuchElementException(String.format("Отсутствует вещь с id = %d", itemId));
    }

    @Override
    public List<Comment> getItemComments(int itemId) {
        log.info("Получен запрос на отправку всех комментариев вещи с id = {}", itemId);

        return commentRepository.getCommentByItemId(itemId);
    }
}
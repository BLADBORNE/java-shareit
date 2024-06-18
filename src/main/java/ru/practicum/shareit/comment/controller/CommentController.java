package ru.practicum.shareit.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreationCommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/items/{itemId}/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto addCommentToItem(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @PathVariable(value = "itemId") Integer itemId,
            @Valid @RequestBody CreationCommentDto dto
    ) {
        CommentDto comment = CommentMapper.toCommentDto(commentService.addCommentToItem(userId, itemId, dto));

        log.info("Пользователь с id = {} успешно добавил отзыв вещи с id = {}", userId, itemId);

        return ResponseEntity.ok().body(comment).getBody();
    }
}
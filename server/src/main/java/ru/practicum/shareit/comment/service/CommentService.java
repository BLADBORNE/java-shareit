package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CreationCommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentService {
    Comment addCommentToItem(int userId, int itemId, CreationCommentDto dto);

    List<Comment> getItemComments(int itemId);
}
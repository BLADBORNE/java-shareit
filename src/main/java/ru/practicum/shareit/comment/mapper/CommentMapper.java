package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreationCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    private CommentMapper() {

    }

    public static Comment toComment(CreationCommentDto creationCommentDto, User user, Item item) {
        Comment comment = new Comment();

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(creationCommentDto.getText());
        comment.setCreated(LocalDateTime.now());

        return comment;

    }

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }
}
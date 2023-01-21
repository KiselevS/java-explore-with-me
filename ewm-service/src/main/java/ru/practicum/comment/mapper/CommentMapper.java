package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentOutDto;
import ru.practicum.comment.model.Comment;

public class CommentMapper {
    public static CommentOutDto toCommentOutDto(Comment comment) {
        return CommentOutDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}

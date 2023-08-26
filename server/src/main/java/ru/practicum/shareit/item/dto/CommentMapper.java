package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Comment;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {

    public CommentDto commentToDto(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment can not be null.");
        }

        return CommentDto.builder()
                .text(comment.getText())
                .build();
    }

    public CommentResponseDto toResponseDto(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment can not be null.");
        }

        return CommentResponseDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public Comment dtoToComment(CommentDto commentDto) {
        if (commentDto == null) {
            throw new IllegalArgumentException("CommentDto can not be null.");
        }

        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }

    public List<CommentResponseDto> listCommentsToListResponse(Collection<Comment> comments) {
        return comments.stream().map(CommentMapper::toResponseDto).collect(Collectors.toList());
    }
}

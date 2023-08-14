package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private User author;
}

package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private User author;
}

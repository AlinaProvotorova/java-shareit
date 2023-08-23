package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
public class CommentDto {
    @NotNull(groups = {Marker.OnUpdate.class})
    private Long id;
    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 1000, groups = {Marker.OnUpdate.class, Marker.OnCreate.class})
    private String text;
}

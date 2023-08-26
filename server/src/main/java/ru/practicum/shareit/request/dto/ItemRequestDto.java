package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ItemRequestDto {

    private Long id;
    private String description;
}

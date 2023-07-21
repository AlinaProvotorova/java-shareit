package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
public class ItemDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
}

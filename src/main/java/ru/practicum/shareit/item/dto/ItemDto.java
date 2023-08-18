package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private Boolean available;
    private Long requestId;
}

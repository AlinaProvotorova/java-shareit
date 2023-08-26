package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
@ToString
public class ItemRequestDto {

    private Long id;
    @NotBlank
    @Size(max = 1000)
    private String description;
}

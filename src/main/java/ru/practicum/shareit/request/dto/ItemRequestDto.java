package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class ItemRequestDto {

    private Long id;

    @NotNull
    private String description;

    private LocalDateTime created;
}

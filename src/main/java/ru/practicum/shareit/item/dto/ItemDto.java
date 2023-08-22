package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotNull(groups = {Marker.OnUpdate.class})
    private Long id;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 255)
    private String name;

    @NotBlank(groups = {Marker.OnCreate.class})
    @Size(max = 512)
    private String description;

    @NotNull(groups = {Marker.OnCreate.class})
    private Boolean available;
    private Long requestId;
}

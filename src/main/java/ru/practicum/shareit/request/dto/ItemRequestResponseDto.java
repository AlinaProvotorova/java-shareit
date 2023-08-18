package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Setter
@EqualsAndHashCode
@ToString
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseDto> items;

    public static ItemRequestResponseDto create(ItemRequest itemRequest, List<ItemResponseDto> items) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

}

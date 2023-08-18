package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

public class ItemRequestMapper {

    private ItemRequestMapper() {
    }

    public static ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest can not be null.");
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            throw new IllegalArgumentException("ItemRequestDto can not be null.");
        }

        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static ItemRequestResponseDto toItemRequestResponse(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest can not be null.");
        }

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }


}

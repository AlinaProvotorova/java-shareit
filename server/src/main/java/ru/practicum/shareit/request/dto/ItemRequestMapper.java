package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {


    public ItemRequestDto itemRequestToDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest can not be null.");
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .build();
    }

    public ItemRequest dtoToItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            throw new IllegalArgumentException("ItemRequestDto can not be null.");
        }

        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .build();
    }

    public ItemRequestResponseDto toItemRequestResponse(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IllegalArgumentException("ItemRequest can not be null.");
        }

        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequestResponseDto listItemResponseToItemRequestResponse(ItemRequest itemRequest, List<ItemResponseDto> items) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }


}

package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null.");
        }
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static Item dtoToItem(ItemDto itemDto) {
        if (itemDto == null) {
            throw new IllegalArgumentException("ItemDto не может быть null.");
        }
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item dtoToItem(ItemDto itemDto, Item item) {
        if (itemDto == null) {
            throw new IllegalArgumentException("ItemDto не может быть null.");
        }
        item.setName(itemDto.getName() != null && !itemDto.getName().isBlank() ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null && !itemDto.getDescription().isBlank() ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());
        return item;
    }

    public static ItemResponseDto toResponseItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item can not be null.");
        }
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(item.getId())
                .available(item.getAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .build();
        if (item.getRequest() != null) {
            itemResponseDto.setRequestId(item.getRequest().getId());
        }
        return itemResponseDto;
    }

    public static List<ItemResponseDto> listItemsToListResponseDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::toResponseItem).collect(Collectors.toList());
    }
}

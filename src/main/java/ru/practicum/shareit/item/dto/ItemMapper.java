package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null.");
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static Item dtoToItem(ItemDto item) {
        if (item == null) {
            throw new IllegalArgumentException("ItemDto не может быть null.");
        }
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }
}

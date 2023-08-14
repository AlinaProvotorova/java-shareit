package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

interface ItemService {

    List<ItemDto> getAllItems();

    List<ItemResponseDto> getOwnersItems(Long userId);

    List<ItemDto> searchBy(String text, Long userId);

    ItemResponseDto getItemById(Long id, Long userId);

    ItemDto saveNewItem(Long userId, ItemDto item);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    void deleteItem(Long id, Long userId);

    CommentResponseDto addComment(CommentDto commentDto, long itemId, long userId);
}

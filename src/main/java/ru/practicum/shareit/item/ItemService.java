package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

interface ItemService {

    List<ItemDto> getAllItems();

    List<ItemDto> getOwnersItems(Integer userId);

    List<ItemDto> searchBy(String text);

    ItemDto getItemById(Integer id);

    ItemDto saveNewItem(Integer userId, ItemDto item);

    ItemDto updateItem(Integer itemId, Integer userId, ItemDto itemDto);

    void deleteItem(Integer id);
}

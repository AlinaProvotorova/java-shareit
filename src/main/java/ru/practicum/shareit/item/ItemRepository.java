package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Map;

interface ItemRepository {

    List<Item> getAllItems();

    Map<Integer, Item> getItemsMap();

    List<Item> getOwnersItems(User user);

    List<Item> searchBy(String text);

    Item getItemById(Integer id);

    Item saveNewItem(User user, Item item);

    Item updateItem(Integer itemId, Item item);

    void deleteItem(Integer id);
}

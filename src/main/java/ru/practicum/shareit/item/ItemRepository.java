package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;

interface ItemRepository {

    List<Item> getAllItems();

    List<Item> getOwnersItems(User user);

    List<Item> searchBy(String text);

    Item getItemById(Integer id);

    Item saveNewItem(User user, Item item);

    Item updateItem(Integer itemId, Item item);

    void deleteItem(Integer id);
}

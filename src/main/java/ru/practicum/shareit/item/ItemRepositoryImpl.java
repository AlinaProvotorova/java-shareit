package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer counterId = 0;

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Map<Integer, Item> getItemsMap() {
        return items;
    }

    @Override
    public List<Item> getOwnersItems(User user) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchBy(String text) {
        return items.values().stream()
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                        (item.getDescription() != null && item.getDescription()
                                .toLowerCase()
                                .contains(text.toLowerCase())) &&
                                item.getAvailable()
                )
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Integer id) {
        return items.get(id);
    }

    @Override
    public Item saveNewItem(User user, Item item) {
        item.setId(++counterId);
        item.setOwner(user);
        items.put(counterId, item);
        return item;
    }

    @Override
    public Item updateItem(Integer itemId, Item item) {
        items.put(itemId, item);
        return item;
    }

    @Override
    public void deleteItem(Integer id) {
        items.remove(id);
    }
}

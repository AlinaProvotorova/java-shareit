package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getAllItems() {
        log.info("Получен список всех существующих Item.");
        return itemRepository.getAllItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getOwnersItems(Integer userId) {
        UserValidate.checkUserId(userRepository.getAllUsers(), userId);
        log.info("Получен список всех существующих Item для пользователя c ID {}.", userId);
        return itemRepository.getOwnersItems(userRepository.getUserById(userId))
                .stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchBy(String text) {
        if (text == null || text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        log.info("Выполнен поиск по Item с текстом {}", text);
        return itemRepository.searchBy(text).stream().map(
                ItemMapper::toItemDto).collect(Collectors.toList()
        );
    }

    @Override
    public ItemDto getItemById(Integer id) {
        ItemValidate.checkItemId(itemRepository.getAllItems(), id);
        log.info("Получен Item с ID {}.", id);
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    @Override
    public ItemDto saveNewItem(Integer userId, ItemDto item) {
        UserValidate.checkUserId(userRepository.getAllUsers(), userId);
        ItemValidate.checkItemAvailable(item);
        Item newItem = itemRepository.saveNewItem(userRepository.getUserById(userId), ItemMapper.dtoToItem(item));
        log.info("Пользователь с ID {} создал Item c ID {}.", userId, newItem.getId());
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(Integer itemId, Integer userId, ItemDto item) {
        UserValidate.checkUserId(userRepository.getAllUsers(), userId);
        ItemValidate.checkItemId(itemRepository.getAllItems(), itemId);
        ItemValidate.checkOwnerItem(itemRepository.getItemById(itemId), userId);
        Item updater = itemRepository.updateItem(itemId, ItemMapper.dtoToItem(item));
        log.info("User(Owner) c ID {} обновил данные Item c ID {}.", userId, updater.getId());
        return ItemMapper.toItemDto(updater);
    }

    @Override
    public void deleteItem(Integer id) {
        ItemValidate.checkItemId(itemRepository.getAllItems(), id);
        log.info("Item с ID {} удален.", id);
        itemRepository.deleteItem(id);
    }
}

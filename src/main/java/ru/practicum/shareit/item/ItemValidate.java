package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
public class ItemValidate {
    static String NOT_FOUND_ID = "Вещи с ID %d не существует";
    static String NOT_FOUND_OWNER = "У пользователя с ID %d вещи с ID %d не существует";
    static String NOT_NULL_AVAILABLE = "Поле available не может быть пустым";

    public static void checkOwnerItem(Item item, Integer userid) {
        if (!(item.getOwner().getId().equals(userid))) {
            log.info(NOT_FOUND_OWNER);
            throw new NotFoundException(String.format(NOT_FOUND_OWNER, userid, item.getId()));
        }
    }

    public static void checkItemId(List<Item> items, Integer id) {
        boolean userExists = items.stream().anyMatch(item -> item.getId().equals(id));
        if (!userExists) {
            log.info(NOT_FOUND_ID);
            throw new NotFoundException(String.format(NOT_FOUND_ID, id));
        }
    }

    public static void checkItemAvailable(ItemDto item) {
        if (item.getAvailable() == null || !item.getAvailable()) {
            log.info(NOT_NULL_AVAILABLE);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_NULL_AVAILABLE);
        }
    }
}

package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemValidate {
    private static final String NOT_FOUND_OWNER = "У пользователя с ID %d вещи с ID %d не существует";
    private static final String NOT_NULL_AVAILABLE = "Поле available не может быть пустым";

    public static void checkOwnerItem(Item item, Long userid) {
        if (!(item.getOwner().getId().equals(userid))) {
            throw new NotFoundException(String.format(NOT_FOUND_OWNER, userid, item.getId()));
        }
    }

    public static void checkItemAvailable(ItemDto item) {
        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NOT_NULL_AVAILABLE);
        }
    }
}

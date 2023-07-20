package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
public class UserValidate {
    static String EMAIL_DUPLICATE = "Email %s уже существует. Запрос отклонен";
    static String NOT_FOUND_ID = "Пользователя с ID %d не существует";

    public static void checkEmail(List<User> users, UserDto user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            for (User value : users) {
                if (user.getEmail().equals(value.getEmail())) {
                    log.info(String.format(EMAIL_DUPLICATE, user.getEmail()));
                    throw new EmailDuplicateException(
                            String.format(EMAIL_DUPLICATE, user.getEmail())
                    );
                }
            }
        }
    }

    public static void checkUserId(List<User> users, Integer id) {
        boolean userExists = users.stream()
                .anyMatch(user -> user.getId().equals(id));
        if (!userExists) {
            log.info(String.format(NOT_FOUND_ID, id));
            throw new NotFoundException(String.format(NOT_FOUND_ID, id));
        }
    }

}

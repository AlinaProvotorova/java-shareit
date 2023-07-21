package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Set;

@Slf4j
public class UserValidate {
    private static final String EMAIL_DUPLICATE = "Email %s уже существует. Запрос отклонен";
    private static final String NOT_FOUND_ID = "Пользователя с ID %d не существует";

    public static void checkEmail(List<User> users, UserDto user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            users.stream()
                    .filter(value -> value.getEmail().equals(user.getEmail()))
                    .findFirst()
                    .ifPresent(value -> {
                        log.info(String.format(EMAIL_DUPLICATE, user.getEmail()));
                        throw new EmailDuplicateException(
                                String.format(EMAIL_DUPLICATE, user.getEmail())
                        );
                    });
        }
    }


    public static void checkUserId(Set<Integer> usersId, Integer id) {
        if (!usersId.contains(id)) {
            log.info(String.format(NOT_FOUND_ID, id));
            throw new NotFoundException(String.format(NOT_FOUND_ID, id));
        }
    }


}

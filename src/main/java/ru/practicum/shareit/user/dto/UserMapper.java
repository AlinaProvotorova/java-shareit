package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepositoryImpl;

public class UserMapper {
    private static final UserRepositoryImpl userRepository = new UserRepositoryImpl();

    public static UserDto toUserDto(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User не может быть null");
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User dtoToUser(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto не может быть null.");
        }
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User dtoToUser(UserDto userDto, User user) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto не может быть null.");
        }
        user.setName(userDto.getName() != null && !userDto.getName().isBlank() ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null && !userDto.getEmail().isBlank() ? userDto.getEmail() : user.getEmail());
        return user;
    }

}

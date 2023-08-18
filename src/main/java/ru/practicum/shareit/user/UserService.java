package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto saveNewUser(UserDto user);

    UserDto updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);
}

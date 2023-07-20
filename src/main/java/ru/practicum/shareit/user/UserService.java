package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Integer id);

    UserDto saveNewUser(UserDto user);

    UserDto updateUser(Integer id, UserDto userDto);

    void deleteUser(Integer id);
}

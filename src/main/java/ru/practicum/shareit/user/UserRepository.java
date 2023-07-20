package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User getUserById(Integer id);

    User saveNewUser(User user);

    User updateUser(Integer id, User user);

    void deleteUser(Integer id);
}

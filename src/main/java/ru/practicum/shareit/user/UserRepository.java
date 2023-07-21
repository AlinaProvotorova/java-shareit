package ru.practicum.shareit.user;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    List<User> getAllUsers();

    Map<Integer, User> getUsersMap();

    User getUserById(Integer id);

    User saveNewUser(User user);

    User updateUser(Integer id, User user);

    void deleteUser(Integer id);
}

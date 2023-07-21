package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private static Integer counterId = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }


    @Override
    public Map<Integer, User> getUsersMap() {
        return users;
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public User saveNewUser(User user) {
        user.setId(++counterId);
        users.put(counterId, user);
        return user;
    }

    @Override
    public User updateUser(Integer id, User user) {
        users.put(id, user);
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

}

package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final Map<Integer, User> USERS = new HashMap<>();
    private static Integer counterId = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(USERS.values());
    }

    @Override
    public User getUserById(Integer id) {
        return USERS.get(id);
    }

    @Override
    public synchronized User saveNewUser(User user) {
        user.setId(++counterId);
        USERS.put(counterId, user);
        return user;
    }

    @Override
    public User updateUser(Integer id, User user) {
        User updateUser = USERS.get(id);
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updateUser.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            updateUser.setName(user.getName());
        }
        USERS.put(id, updateUser);
        return updateUser;
    }

    @Override
    public void deleteUser(Integer id) {
        USERS.remove(id);
    }
}

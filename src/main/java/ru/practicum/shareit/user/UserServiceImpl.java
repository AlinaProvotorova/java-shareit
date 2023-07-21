package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Список всех существующих User получен.");
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Integer id) {
        UserValidate.checkUserId(userRepository.getUsersMap().keySet(), id);
        log.info("Пользователь с ID {} получен .", id);
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto saveNewUser(UserDto user) {
        UserValidate.checkEmail(userRepository.getAllUsers(), user);
        User newUser = userRepository.saveNewUser(UserMapper.dtoToUser(user));
        log.info("Пользователь c ID {} создан.", newUser.getId());
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(Integer id, UserDto userDto) {
        UserValidate.checkUserId(userRepository.getUsersMap().keySet(), id);
        User user = userRepository.getUsersMap().get(id);
        UserValidate.checkEmail(
                userRepository.getAllUsers().stream()
                        .filter(u -> !u.getId().equals(id))
                        .collect(Collectors.toList()),
                userDto
        );
        User updateUser = userRepository.updateUser(id, UserMapper.dtoToUser(userDto, user));
        log.info("Данные пользователя обновлёны {}.", updateUser);
        return UserMapper.toUserDto(updateUser);
    }

    @Override
    public void deleteUser(Integer id) {
        UserValidate.checkUserId(userRepository.getUsersMap().keySet(), id);
        log.info("Пользователь с ID {} удален .", id);
        userRepository.deleteUser(id);
    }
}

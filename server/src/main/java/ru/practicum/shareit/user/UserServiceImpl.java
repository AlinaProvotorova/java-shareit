package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Constants.EMAIL_DUPLICATE;
import static ru.practicum.shareit.utils.Constants.USER_NOT_FOUND;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public List<UserDto> getAllUsers() {
        log.info("Список всех существующих User получен.");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, id))
        );
        log.info("Пользователь с ID {} получен .", id);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto saveNewUser(UserDto user) {
        try {
            User newUser = userRepository.save(UserMapper.dtoToUser(user));
            log.info("Пользователь c ID {} создан.", newUser.getId());
            return UserMapper.toUserDto(newUser);
        } catch (ConstraintViolationException e) {
            throw new EmailDuplicateException(
                    String.format(EMAIL_DUPLICATE, user.getEmail())
            );
        }
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new NotFoundException(String.format(USER_NOT_FOUND, id))
            );
            User updateUser = userRepository.saveAndFlush(UserMapper.dtoToUser(userDto, user));
            log.info("Данные пользователя обновлёны {}.", updateUser);
            return UserMapper.toUserDto(updateUser);
        } catch (ConstraintViolationException e) {
            throw new EmailDuplicateException(
                    String.format(EMAIL_DUPLICATE, userDto.getEmail())
            );
        }

    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, id))
        );
        log.info("Пользователь с ID {} удален .", id);
        userRepository.deleteById(id);
    }
}

package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailDuplicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Transactional
@DisplayName("Тесты класса UserService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private User mockUser1;
    @Mock
    UserRepository userRepository;
    UserServiceImpl userService;

    private MockitoSession session;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        userService = new UserServiceImpl(userRepository);
        mockUser1 = new User(1L, "Test1", "test@yandex.ru");
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(userService.getAllUsers().isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserById() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto result = userService.getUserById(userId);
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Тест на создание User")
    public void testSaveNewUser() {
        UserDto userDto = UserMapper.toUserDto(mockUser1);
        when(userRepository.save(any())).thenReturn(mockUser1);
        UserDto result = userService.saveNewUser(userDto);
        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Тест на создание User c дубликатом Email")
    public void testSaveNewUser_DuplicateEmail() {
        UserDto userDto = UserMapper.toUserDto(mockUser1);
        when(userRepository.save(any())).thenThrow(EmailDuplicateException.class);
        assertThrows(EmailDuplicateException.class, () -> userService.saveNewUser(userDto));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Тест на обновление User")
    public void testUpdateUser() {
        UserDto userDto = UserMapper.toUserDto(mockUser1);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(mockUser1));
        when(userRepository.saveAndFlush(any())).thenReturn(mockUser1);
        UserDto result = userService.updateUser(userDto.getId(), userDto);
        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test
    @DisplayName("Тест на обновление Email User на дублирующий")
    public void testUpdateUser_DuplicateEmail() {
        UserDto userDto = UserMapper.toUserDto(mockUser1);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(new User()));
        when(userRepository.saveAndFlush(any())).thenThrow(EmailDuplicateException.class);
        assertThrows(EmailDuplicateException.class, () -> userService.updateUser(userDto.getId(), userDto));
        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void testDeleteUser() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteUser_NotFound() {
        UserDto userDto = UserMapper.toUserDto(mockUser1);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.deleteUser(userDto.getId()));
        verify(userRepository, times(1)).findById(userDto.getId());
        verify(userRepository, never()).deleteById(userDto.getId());
    }
}

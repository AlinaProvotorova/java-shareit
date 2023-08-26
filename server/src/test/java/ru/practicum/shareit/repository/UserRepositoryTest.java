package ru.practicum.shareit.repository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {

    User user = User.builder()
            .name("Test")
            .email("test@yandex.ru")
            .build();
    private final UserRepository userRepository;

    @Test
    @DisplayName("Тест на создание нового User")
    @SneakyThrows
    @Transactional
    @Rollback
    void createUserTest() {
        User actual = userRepository.save(user);
        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("Тест на удаление существующего User")
    @SneakyThrows
    @Transactional
    @Rollback
    void deleteUserTest() {
        User actual = userRepository.save(user);
        Assertions.assertEquals(user, actual);
        userRepository.delete(actual);

        Optional<User> deletedUser = userRepository.findById(1L);
        Assertions.assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Тест обнаружение сущестующего User")
    @SneakyThrows
    @Transactional
    @Rollback
    void userExistTest() {
        User actual = userRepository.save(user);
        Assertions.assertTrue(userRepository.existsById(actual.getId()));
    }

    @Test
    @DisplayName("Тест проверки дублирования Email при создании User")
    @Transactional
    @Rollback
    @SneakyThrows
    void userEmailDuplicateTest() {
        User user1 = User.builder()
                .id(4L)
                .name("Иван")
                .email("ivan@mail.ru")
                .build();
        userRepository.save(user1);

        User user2 = User.builder()
                .id(5L)
                .name("Петр")
                .email("ivan@mail.ru")
                .build();
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user2));
    }


}
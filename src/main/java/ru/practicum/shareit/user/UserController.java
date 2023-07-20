package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = UserController.USERS_ENDPOINT)
public class UserController {
    public static final String USERS_ENDPOINT = "/users";

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable @Positive Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto user) {
        return userService.saveNewUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable @Positive Integer id, @RequestBody UserDto user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable @Positive Integer id) {
        userService.deleteUser(id);
        return String.format("Пользователь %d удален", id);
    }
}


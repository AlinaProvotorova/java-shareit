package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = UserController.USERS_ENDPOINT)
@Validated
public class UserController {
    public static final String USERS_ENDPOINT = "/users";

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable @Positive Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public UserDto saveNewUser(@RequestBody @Valid UserDto user) {
        return userService.saveNewUser(user);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public UserDto updateUser(@PathVariable @Positive Long id, @RequestBody @Valid UserDto user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable @Positive Long id) {
        userService.deleteUser(id);
        return String.format("Пользователь %d удален", id);
    }
}


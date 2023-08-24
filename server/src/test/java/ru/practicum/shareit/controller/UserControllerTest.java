package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
@DisplayName("Тесты класса UserController")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    final ObjectMapper objectMapper;
    final MockMvc mockMvc;
    @MockBean
    UserService userService;

    private static final User mockUser1 = User.builder().id(1L).name("Ivan").email("ivan@mail.ru").build();
    private static final User mockUser2 = User.builder().id(2L).name("Petr").email("petr@mail.ru").build();

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение всех User")
    @SneakyThrows
    void geAllUsersTest() {
        UserDto userDto1 = UserMapper.toUserDto(mockUser1);
        UserDto userDto2 = UserMapper.toUserDto(mockUser2);
        when(userService.saveNewUser(userDto1)).thenReturn(userDto1);
        when((userService.getUserById(1L))).thenReturn(userDto1);
        when(userService.saveNewUser(userDto2)).thenReturn(userDto2);
        when((userService.getUserById(2L))).thenReturn(userDto2);
        userService.saveNewUser(userDto1);
        userService.saveNewUser(userDto2);
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение User по ID")
    @SneakyThrows
    void getUserByIdTest() {
        User user = mockUser1;
        UserDto userDto = UserMapper.toUserDto(user);
        when(userService.saveNewUser(any())).thenReturn(userDto);
        when((userService.getUserById(any()))).thenReturn(userDto);
        userService.saveNewUser(userDto);
        mockMvc.perform(get("/users/{id}", user.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).getUserById(userDto.getId());
    }

    @Test
    @DisplayName("Тест на эндпоинт  @PostMapping создания нового User")
    void saveNewUser() throws Exception {
        UserDto userDto = UserMapper.toUserDto(mockUser1);
        when(userService.saveNewUser(any(UserDto.class))).thenReturn(userDto);
        userService.saveNewUser(userDto);
        mockMvc.perform(
                        post("/users", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

//    @Test
//    @DisplayName("Тест на эндпоинт  @PostMapping создания нового User с некорректным форматом почты")
//    void saveNewUserValidate() throws Exception {
//        UserDto userDto = UserMapper.toUserDto(mockUser1);
//        userDto.setEmail("email");
//        when(userService.saveNewUser(any(UserDto.class))).thenReturn(userDto);
//        userService.saveNewUser(userDto);
//        mockMvc.perform(
//                        post("/users", 1L)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(userDto))
//                                .characterEncoding(StandardCharsets.UTF_8)
//                                .accept(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isBadRequest())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//    }

    @Test
    @DisplayName("Тест на эндпоинт @PatchMapping на одновление User по ID")
    @SneakyThrows
    void updateUserTest() {
        UserDto userDto1 = UserMapper.toUserDto(mockUser1);
        User user2 = mockUser2;
        user2.setId(1L);
        UserDto userDto2 = UserMapper.toUserDto(user2);
        when(userService.saveNewUser(userDto1)).thenReturn(userDto1);
        when((userService.getUserById(1L))).thenReturn(userDto1);
        when(userService.saveNewUser(userDto2)).thenReturn(userDto2);
        when((userService.getUserById(2L))).thenReturn(userDto2);
        when(userService.updateUser(1L, userDto2)).thenReturn(userDto2);
        mockMvc.perform(
                        patch("/users/{userId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto2))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("Тест на эндпоинт @DeleteMapping на удаление User по ID")
    @SneakyThrows
    void deleteUserTest() {
        UserDto userDto = UserMapper.toUserDto(mockUser1);
        when(userService.saveNewUser(any())).thenReturn(userDto);
        when((userService.getUserById(any()))).thenReturn(userDto);
        userService.saveNewUser(userDto);
        mockMvc.perform(delete("/users/{userId}", userDto.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).deleteUser(1L);
    }
}
package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@DisplayName("Тесты класса ItemController")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {
    final ObjectMapper objectMapper;
    final MockMvc mockMvc;
    @MockBean
    ItemService itemService;

    private static final User mockUser1 = User.builder().id(1L).name("Ivan").email("ivan@mail.ru").build();
    private static final User mockUser2 = User.builder().id(2L).name("Petr").email("petr@mail.ru").build();
    private static final Item mockItem1 = Item.builder()
            .id(1L).name("Молоток").description("Заколачивает гвозди").available(true).owner(mockUser1).build();
    private static final Item mockItem2 = Item.builder()
            .id(2L).name("Шуруповерт").description("Закручивает шурупы").available(true).owner(mockUser2).build();
    private static final Booking mockBooking1 = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2021, 12, 12, 1, 1))
            .end(LocalDateTime.of(2021, 12, 22, 1, 1))
            .booker(mockUser2).item(mockItem1).status(BookingStatus.APPROVED)
            .build();
    private static final Booking mockBooking2 = Booking.builder()
            .id(2L)
            .start(LocalDateTime.of(2021, 12, 12, 1, 1))
            .end(LocalDateTime.of(2021, 12, 22, 1, 1))
            .booker(mockUser2).item(mockItem1).status(BookingStatus.APPROVED)
            .build();

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение всех Item для User Owner")
    @SneakyThrows
    void geAllItemsTest() {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/all", 3, 2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(itemService).getAllItems();
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение всех Item для User Owner")
    @SneakyThrows
    void getOwnersItemsTest() {
        User owner = mockUser1;
        mockItem2.setOwner(owner);
        mockMvc.perform(MockMvcRequestBuilders.get("/items?from={from}&size={size}", 3, 2)
                        .header("X-Sharer-User-Id", owner.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(itemService).getOwnersItems(3, 2, owner.getId());
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на получение Item по ID")
    @SneakyThrows
    void getItemByIdTest() {
        ItemDto itemDto = ItemMapper.toItemDto(mockItem1);
        ItemResponseDto itemResponseDto = ItemResponseDto.create(mockBooking1, mockBooking2, mockItem1, List.of());
        when(itemService.saveNewItem(Mockito.any(), Mockito.any())).thenReturn(itemDto);
        when((itemService.getItemById(Mockito.any(), Mockito.any()))).thenReturn(itemResponseDto);
        itemService.saveNewItem(1L, itemDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(itemService).getItemById(itemDto.getId(), 1L);
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping на поиск всех Item по текстовому запросу")
    @SneakyThrows
    void searchByTest() {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("from", "0")
                        .param("size", "10")
                        .param("text", "Серп")
                        .header("X-Sharer-User-Id", mockUser1.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(itemService).searchBy("Серп", 1L, 0, 10);
    }

    @Test
    @DisplayName("Тест на эндпоинт @PostMapping создания Item")
    @SneakyThrows
    void saveNewItemTest() {
        ItemDto itemDto = ItemMapper.toItemDto(mockItem1);
        ItemResponseDto itemResponseDto = ItemResponseDto.create(mockBooking1, mockBooking2, mockItem1, List.of());
        when(itemService.saveNewItem(Mockito.any(), Mockito.any())).thenReturn(itemDto);
        when((itemService.getItemById(Mockito.any(), Mockito.any()))).thenReturn(itemResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        verify(itemService).saveNewItem(1L, itemDto);
    }


    @Test
    @DisplayName("Тест на эндпоинт @PatchMapping на одновление Item по ID")
    @SneakyThrows
    void updateItemTest() {
        ItemDto itemDto = ItemMapper.toItemDto(mockItem1);
        ItemResponseDto itemResponseDto = ItemResponseDto.create(mockBooking1, mockBooking2, mockItem1, List.of());
        when(itemService.saveNewItem(Mockito.any(), Mockito.any())).thenReturn(itemDto);
        when((itemService.getItemById(Mockito.any(), Mockito.any()))).thenReturn(itemResponseDto);
        when((itemService.updateItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemDto.class)))).thenReturn(itemDto);
        itemService.saveNewItem(1L, itemDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("Тест на эндпоинт @DeleteMapping на удаленчие Item по ID")
    @SneakyThrows
    void deleteItemTest() {
        ItemDto itemDto = ItemMapper.toItemDto(mockItem1);
        ItemResponseDto itemResponseDto = ItemResponseDto.create(mockBooking1, mockBooking2, mockItem1, List.of());
        when(itemService.saveNewItem(Mockito.any(), Mockito.any())).thenReturn(itemDto);
        when((itemService.getItemById(Mockito.any(), Mockito.any()))).thenReturn(itemResponseDto);
        itemService.saveNewItem(1L, itemDto);
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(itemService).deleteItem(1L, 1L);
    }


    @Test
    @DisplayName("Тест на эндпоинт @PostMapping создания Comment")
    @SneakyThrows
    void addCommentTest() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Comment")
                .item(mockItem1)
                .author(mockUser1)
                .build();
        CommentResponseDto commentResponseDto = CommentMapper.toResponseDto(comment);
        when(itemService.addComment(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong()))
                .thenReturn(commentResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @DisplayName("Тест на ItemMapper с исключением Dto.")
    public void dtoToItemNullDtoTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            ItemMapper.dtoToItem(null);
        });
    }

    @Test
    @DisplayName("Тест на ItemMapper с исключением Dto.")
    public void itemToDtoNullDtoTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            ItemMapper.toItemDto(null);
        });
    }

    @Test
    @DisplayName("Тест на ItemMapper с исключением Dto.")
    public void toResposeItemNullDtoTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            ItemMapper.toResponseItem(null);
        });
    }

}



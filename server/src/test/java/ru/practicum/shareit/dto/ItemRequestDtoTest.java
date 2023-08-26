package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса ItemRequestDto")
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTesterItemRequestDto;
    @Autowired
    private JacksonTester<ItemRequestResponseDto> jacksonTesterItemRequestResponseDto;


    @Test
    @DisplayName("Тест на сериализацию класса ItemRequestDto")
    @SneakyThrows
    @Rollback(true)
    void itemRequestDtoTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .build();

        JsonContent<ItemRequestDto> itemRequestDtoJsonContent = jacksonTesterItemRequestDto.write(itemRequestDto);

        assertThat(itemRequestDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(itemRequestDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
    }

    @Test
    @DisplayName("Тест на десериализацию класса ItemRequestDto")
    @SneakyThrows
    @Rollback(true)
    void itemRequestDtoReadTest() {
        String json = "{\"id\":1,\"description\":\"Нужна дрель\",\"created\":\"2023-05-12T12:12\"}";

        ItemRequestDto itemRequestDto = jacksonTesterItemRequestDto.parseObject(json);

        assertThat(1L).isEqualTo(itemRequestDto.getId());
        assertThat("Нужна дрель").isEqualTo(itemRequestDto.getDescription());
    }

    @Test
    @DisplayName("Тест на сериализацию класса ItemRequestResponseDto")
    @SneakyThrows
    @Rollback(true)
    void itemRequestResponseDtoTest() {

        User user = User.builder()
                .id(1L)
                .name("Игорь")
                .email("Super@yandex.ru")
                .build();

        User booker = User.builder()
                .id(2L)
                .name("Игорь")
                .email("Super@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(3L)
                .name("Молоток")
                .description("Описание")
                .available(true)
                .owner(user)
                .build();

        Comment comment = Comment.builder()
                .id(4L)
                .author(booker)
                .text("Всё понравилось")
                .item(item)
                .created(LocalDateTime.of(2023, 7, 07, 12, 12))
                .build();


        Booking last = Booking.builder()
                .id(5L)
                .start(LocalDateTime.of(2023, 1, 12, 12, 12))
                .end(LocalDateTime.of(2023, 1, 13, 12, 12))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking next = Booking.builder()
                .id(6L)
                .start(LocalDateTime.of(2023, 10, 12, 12, 12))
                .end(LocalDateTime.of(2023, 10, 13, 12, 12))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        ItemResponseDto itemResponseDto = ItemMapper.listCommenyToItemResponseDto(last, next, item, List.of(comment));

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужна дрель")
                .requester(user)
                .created(LocalDateTime.of(2023, 5, 12, 12, 12, 0))
                .build();


        ItemRequestResponseDto itemRequestResponseDto = ItemRequestMapper.listItemResponseToItemRequestResponse(itemRequest, List.of(itemResponseDto));

        JsonContent<ItemRequestResponseDto> itemRequestResponseDtoJsonContent = jacksonTesterItemRequestResponseDto.write(itemRequestResponseDto);

        assertThat(itemRequestResponseDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(itemRequestResponseDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(itemRequestResponseDtoJsonContent).extractingJsonPathStringValue("$.created").isEqualTo("2023-05-12T12:12:00");
        assertThat(itemRequestResponseDtoJsonContent).extractingJsonPathArrayValue("$.items").isNotEmpty();
    }

}
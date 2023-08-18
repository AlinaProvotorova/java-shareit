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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса ItemDto")
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTesterItemDto;
    @Autowired
    private JacksonTester<ItemResponseDto> jacksonTesterItemResponseDto;

    @Test
    @DisplayName("Тест на сериализацию класса ItemDto")
    @SneakyThrows
    @Rollback(true)
    void itemDtoTest() {


        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Женя")
                .description("Полное описание")
                .available(true)
                .requestId(3L)
                .build();


        JsonContent<ItemDto> itemDtoJsonContent = jacksonTesterItemDto.write(itemDto);

        assertThat(itemDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(itemDtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Женя");
        assertThat(itemDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Полное описание");
        assertThat(itemDtoJsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(itemDtoJsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(3);

    }

    @Test
    @DisplayName("Тест на сериализацию класса ItemResponseDto")
    @SneakyThrows
    @Rollback(true)
    void itemResponseDtoTest() {

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

        ItemResponseDto itemResponseDto = ItemResponseDto.create(last, next, item, List.of(comment));
        JsonContent<ItemResponseDto> itemResponseDtoJsonContent = jacksonTesterItemResponseDto.write(itemResponseDto);


        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.id");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.name");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.description");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.available");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.lastBooking.id");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.lastBooking.bookerId");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.nextBooking.id");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.nextBooking.bookerId");
        assertThat(itemResponseDtoJsonContent).hasJsonPath("$.comments");
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Молоток");
        assertThat(itemResponseDtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(itemResponseDtoJsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(5);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(6);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);
        assertThat(itemResponseDtoJsonContent).extractingJsonPathArrayValue("$.comments").isNotNull();
    }

}
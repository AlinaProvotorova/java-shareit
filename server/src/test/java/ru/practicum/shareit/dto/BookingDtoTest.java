package ru.practicum.shareit.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@DisplayName("Тесты класса BookingDto")
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingResponseDto> jacksonTesterBookingResponseDto;
    @Autowired
    private JacksonTester<BookingRequestDto> jacksonTesterBookingRequestDto;
    @Autowired
    private JacksonTester<BookingShortDto> jacksonTesterBookingShortDto;

    @Test
    @DisplayName("Тест на сериализацию класса BookingResponseDto")
    @SneakyThrows
    void bookingResponseDtoTest() {

        UserDto booker = UserDto.builder()
                .id(2L)
                .name("Игорь")
                .email("Super@yandex.ru")
                .build();

        ItemDto item = ItemDto.builder()
                .id(3L)
                .name("Молоток")
                .description("Описание")
                .available(true)
                .build();

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 1, 12, 12, 12))
                .end(LocalDateTime.of(2023, 1, 13, 12, 12))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        JsonContent<BookingResponseDto> bookingDtoJsonContent = jacksonTesterBookingResponseDto.write(bookingResponseDto);

        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-12T12:12:00");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-13T12:12:00");
        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo("Молоток");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.booker.name").isEqualTo("Игорь");
        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    @DisplayName("Тест на сериализацию класса BookingRequestDto")
    @SneakyThrows
    void bookingRequestDtoTest() {

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.of(2023, 1, 12, 12, 12))
                .end(LocalDateTime.of(2023, 1, 13, 12, 12))
                .itemId(3L)
                .build();

        JsonContent<BookingRequestDto> bookingDtoJsonContent = jacksonTesterBookingRequestDto.write(bookingRequestDto);

        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-12T12:12:00");
        assertThat(bookingDtoJsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-13T12:12:00");
        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(3);

    }

    @Test
    @DisplayName("Тест на сериализацию класса BookingShortDto")
    @SneakyThrows
    void bookingShortDtoTest() {

        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(3L)
                .build();

        JsonContent<BookingShortDto> bookingDtoJsonContent = jacksonTesterBookingShortDto.write(bookingShortDto);

        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(bookingDtoJsonContent).extractingJsonPathNumberValue("$.bookerId").isEqualTo(3);

    }
}

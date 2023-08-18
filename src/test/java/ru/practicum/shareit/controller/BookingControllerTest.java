package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.Constants.HEADER_USER_ID_VALUE;


@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@DisplayName("Тесты класса BookingController")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    final ObjectMapper objectMapper;
    final MockMvc mockMvc;
    @MockBean
    BookingService bookingService;

    private static final User mockUser1 = User.builder().id(1L).name("Ivan").email("ivan@mail.ru").build();
    private static final User mockUser2 = User.builder().id(2L).name("Petr").email("petr@mail.ru").build();
    private static final Item mockItem1 = Item.builder()
            .id(1L).name("Молоток").description("Заколачивает гвозди").available(true).owner(mockUser1).build();
    private static final Booking mockBooking1 = Booking.builder()
            .id(1L)
            .start(LocalDateTime.of(2021, 12, 12, 1, 1))
            .end(LocalDateTime.of(2021, 12, 22, 1, 1))
            .booker(mockUser2).item(mockItem1).status(BookingStatus.APPROVED)
            .build();

    @Test
    @DisplayName("Тест на эндпоинт @PostMapping создания Booking")
    @SneakyThrows
    void bookingCreateTest() {
        Booking booking = mockBooking1;
        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(booking.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusMinutes(30))
                .build();
        BookingResponseDto bookingResponseDto = BookingMapper.bookingToResponse(booking);

        Mockito
                .when(bookingService.createBooking(Mockito.any(), Mockito.anyLong()))
                .thenReturn(bookingResponseDto);

        bookingService.createBooking(bookingRequestDto, 1L);

        mockMvc.perform(post("/bookings")
                        .header(HEADER_USER_ID_VALUE, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(bookingService).createBooking(bookingRequestDto, 1L);
    }

    @Test
    @DisplayName("Тест на эндпоинт @PatchMapping подтврждение Booking от Owner")
    @SneakyThrows
    void confirmTest() {
        User user = mockUser1;
        Booking booking = mockBooking1;
        boolean approved = true;
        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", booking.getId(), approved)
                        .header(HEADER_USER_ID_VALUE, user.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookingService).updateBookingStatus(booking.getId(), user.getId(), approved);
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping получения Booking по ID")
    @SneakyThrows
    void getByIdTest() {
        User user = mockUser1;
        Booking booking = mockBooking1;

        mockMvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header(HEADER_USER_ID_VALUE, user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingService).getBookingById(booking.getId(), user.getId());
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping получение всех своих Booking от User booker")
    @SneakyThrows
    void getAllByBookerTest() {
        User user = mockUser1;
        mockMvc.perform(get("/bookings")
                        .header(HEADER_USER_ID_VALUE, user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingService).getUserBookings(user.getId(), "ALL", 0, 10);
    }

    @Test
    @DisplayName("Тест на эндпоинт @GetMapping получение всех своих Booking от User owner")
    @SneakyThrows
    void getAllByOwnerTest() {
        User user = mockUser1;
        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID_VALUE, user.getId()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookingService).getOwnerBookings(user.getId(), "ALL", 0, 10);
    }

}
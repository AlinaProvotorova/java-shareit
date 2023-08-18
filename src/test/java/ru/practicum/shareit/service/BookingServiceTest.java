package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnknownStateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@DisplayName("Тесты класса BookingService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private User mockUser1;
    private User mockUser2;
    private Item mockItem1;
    private Booking mockBooking1;
    private Booking mockBooking2;

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingServiceImpl;

    private MockitoSession session;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        bookingServiceImpl = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        mockUser1 = new User(1L, "Test1", "test@yandex.ru");
        mockUser2 = new User(2L, "Test2", "test@yandex.ru");
        mockItem1 = Item.builder()
                .id(1L)
                .name("Test1")
                .description("test")
                .available(true)
                .owner(mockUser1)
                .build();
        mockBooking1 = new Booking(1L, LocalDateTime.of(2021, 12, 12, 1, 1), LocalDateTime.of(2021, 12, 22, 1, 1), mockItem1, mockUser2, BookingStatus.APPROVED);
        mockBooking2 = new Booking(2L, LocalDateTime.of(2024, 12, 12, 1, 1), LocalDateTime.of(2024, 12, 22, 1, 1), mockItem1, mockUser2, BookingStatus.APPROVED);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    @DisplayName("Тест на создание Booking")
    public void createBookingTest() {
        User user = mockUser2;
        Item item = mockItem1;
        BookingRequestDto bookingRequestDto = BookingMapper.bookingToRequest(mockBooking1);
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        Booking booking = BookingMapper.requestToBooking(bookingRequestDto);
        booking.setItem(item);
        booking.getItem().setAvailable(true);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);
        BookingResponseDto result = bookingServiceImpl.createBooking(bookingRequestDto, user.getId());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(booking.getId());
    }

    @Test
    @DisplayName("Тест createBooking_whenItemIsNotAvailable_shouldThrowIllegalArgumentException")
    void createBooking_whenItemIsNotAvailable_shouldThrowIllegalArgumentException() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(7);
        User user = mockUser1;
        Item item = mockItem1;
        BookingRequestDto bookingRequestDto = new BookingRequestDto(item.getId(), start, end);
        item.setOwner(user);
        item.setAvailable(false);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(IllegalArgumentException.class, () -> {
            bookingServiceImpl.createBooking(bookingRequestDto, user.getId());
        });
    }

    @Test
    @DisplayName("Тест createBooking_whenStartIsAfterEnd_shouldThrowIllegalArgumentException")
    void createBooking_whenStartIsAfterEnd_shouldThrowResponseStatusException() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        User user = mockUser1;
        Item item = mockItem1;
        item.setOwner(user);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(item.getId(), start, end);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(IllegalArgumentException.class, () -> {
            bookingServiceImpl.createBooking(bookingRequestDto, user.getId());
        });
    }

    @Test
    @DisplayName("Тест createBooking_whenStartIsBeforeNow_shouldThrowIllegalArgumentException")
    void createBooking_whenStartIsBeforeNow_shouldThrowResponseStatusException() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, start, end);
        User user = mockUser1;
        Item item = mockItem1;
        item.setOwner(user);
        item.setAvailable(true);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(IllegalArgumentException.class, () -> {
            bookingServiceImpl.createBooking(bookingRequestDto, 1L);
        });
    }

    @Test
    @DisplayName("Тест createBooking_whenUserIsOwner_shouldThrowNotFoundException")
    void createBooking_whenUserIsOwner_shouldThrowNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, start, end);
        User user = mockUser1;
        Item item = mockItem1;
        item.setOwner(user);
        item.setAvailable(true);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> {
            bookingServiceImpl.createBooking(bookingRequestDto, 1L);
        });
    }

    @Test
    @DisplayName("Тест createBooking_InvalidStartAndEndTimes_ThrowsIllegalArgumentException")
    public void createBooking_InvalidStartAndEndTimes_ThrowsIllegalArgumentException() {
        BookingRequestDto bookingRequestDto = BookingMapper.bookingToRequest(mockBooking1);
        bookingRequestDto.setStart(LocalDateTime.now().plusHours(2));
        bookingRequestDto.setEnd(LocalDateTime.now().plusHours(1));
        User user = mockUser1;
        Item item = mockItem1;
        item.setAvailable(true);
        item.setOwner(user);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.any())).thenReturn(Optional.of(item));
        assertThrows(IllegalArgumentException.class, () -> {
            bookingServiceImpl.createBooking(bookingRequestDto, 1L);
        });
    }

    @Test
    @DisplayName("Тест testUpdateBookingStatusBooking_Success")
    void testUpdateBookingStatusBooking_Success() {
        Booking booking = mockBooking1;
        booking.setBooker(mockUser2);
        booking.setStatus(BookingStatus.WAITING);
        booking.getItem().setOwner(mockUser1);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(booking.getItem().getOwner()));
        bookingServiceImpl.updateBookingStatus(booking.getId(), booking.getItem().getOwner().getId(), true);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Тест testUpdateBookingStatus_BookingNotFound")
    void testUpdateBookingStatus_BookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.updateBookingStatus(1L, 1L, true));
        assertEquals("Бронирования с ID 1 не существует", exception.getMessage());
    }

    @Test
    @DisplayName("Тест testUpdateBookingStatus_UserOwnerNotFound")
    void testUpdateBookingStatus_UserOwnerNotFound() {
        Booking booking = mockBooking1;
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        Exception exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.updateBookingStatus(1L, 1L, true));
        assertEquals("Пользователя с id 1 не существует", exception.getMessage());
    }

    @Test
    @DisplayName("Тест testUpdateBookingStatus_AlreadyApproved")
    void testUpdateBookingStatus_AlreadyApproved() {
        Booking booking = mockBooking1;
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        User userOwner = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(userOwner));
        assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImpl.updateBookingStatus(1L, 1L, true));
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImpl.updateBookingStatus(1L, 1L, true));
        assertEquals("Невозможно изменить статус. Статус бронирования уже изменен на APPROVED или REJECTED.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Тест testUpdateBookingStatus_InvalidStatus")
    void testUpdateBookingStatus_InvalidStatus() {
        Booking booking = mockBooking1;
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        User userOwner = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(userOwner));
        assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImpl.updateBookingStatus(1L, 1L, false));
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> bookingServiceImpl.updateBookingStatus(1L, 1L, false));
        assertEquals("Невозможно изменить статус. Статус бронирования уже изменен на APPROVED или REJECTED.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Тест testUpdateBookingStatus_Unauthorized")
    void testUpdateBookingStatus_Unauthorized() {
        Booking booking = mockBooking1;
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        User userOwner = mockUser2;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(userOwner));
        assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.updateBookingStatus(1L, 2L, false));
        Exception exception = assertThrows(NotFoundException.class,
                () -> bookingServiceImpl.updateBookingStatus(1L, 2L, false));
        assertEquals("Статус бронирования может быть изменен только владельцем.", exception.getMessage());
    }


    @Test
    @DisplayName("Тест testInvalidBookingStatus")
    public void testInvalidBookingStatus() {
        boolean approved = true;
        Booking booking = mockBooking1;
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        User userOwner = mockUser2;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(userOwner));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingServiceImpl.updateBookingStatus(1L, 1L, approved);
        });
        assertEquals("Невозможно изменить статус. Статус бронирования уже изменен на APPROVED или REJECTED.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Тест testGetBookingByIdExistingBooking")
    public void testGetBookingByIdExistingBooking() {
        Booking booking = mockBooking1;
        Long bookingId = 1L;
        Long userId = 1L;
        BookingResponseDto expectedResponse = BookingMapper.bookingToResponse(booking);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        BookingResponseDto actualResponse = bookingServiceImpl.getBookingById(bookingId, userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetBookingByIdNonExistingBooking")
    public void testGetBookingByIdNonExistingBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        when(bookingRepository.findById(Mockito.any())).thenReturn(null);
        assertThrows(NullPointerException.class, () -> {
            bookingServiceImpl.getBookingById(bookingId, userId);
        });
    }

    @Test
    @DisplayName("Тест testGetBookingByIdDataAccess")
    public void testGetBookingByIdDataAccess() {
        Booking booking = mockBooking1;
        booking.setBooker(mockUser2);
        booking.getItem().setOwner(mockUser1);
        Long bookingId = 1L;
        Long userId = 3L;
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> {
            bookingServiceImpl.getBookingById(bookingId, userId);
        }, "Booking not found");
    }

    @Test
    @DisplayName("Тест testGetBookingByIdCorrectData")
    public void testGetBookingByIdCorrectData() {
        Booking booking = mockBooking1;
        Long bookingId = 1L;
        Long userId = 1L;
        BookingResponseDto expectedResponse = BookingMapper.bookingToResponse(booking);
        when(bookingRepository.findById(Mockito.any())).thenReturn(Optional.of(booking));
        BookingResponseDto actualResponse = bookingServiceImpl.getBookingById(bookingId, userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetUserBookingsAllState")
    public void testGetUserBookingsAllState() {
        User user = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        List<Booking> bookingList = Arrays.asList(mockBooking1, mockBooking2);
        when(bookingRepository.findBookingByBookerOrderByStartDesc(
                Mockito.any(), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "ALL";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(mockBooking1),
                BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getUserBookings(bookerId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetUserBookingsWaitingState")
    public void testGetUserBookingsWaitingState() {
        User user = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        booking2.setStatus(BookingStatus.WAITING);
        List<Booking> bookingList = Arrays.asList(booking, booking2);
        when(bookingRepository.findBookingByBookerAndStatusOrderByStartDesc(
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "WAITING";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(BookingMapper.bookingToResponse(booking),
                BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getUserBookings(bookerId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetUserBookingsREJECTEDState")
    public void testGetUserBookingsREJECTEDState() {
        User user = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        booking.setStatus(BookingStatus.REJECTED);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        booking2.setStatus(BookingStatus.REJECTED);
        List<Booking> bookingList = Arrays.asList(booking, booking2);
        when(bookingRepository.findBookingByBookerAndStatusOrderByStartDesc(
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bookingList);
        int from = 0;
        int size = 10;
        String state = "REJECTED";
        Long bookerId = 1L;
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getUserBookings(bookerId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetUserBookingsCurrentState")
    public void testGetUserBookingsCurrentState() {
        User user = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        when(bookingRepository.findBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(Mockito.any(),
                Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any()))
                .thenReturn(Arrays.asList(booking, booking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getUserBookings(
                1L, "CURRENT", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetUserBookingsPastState")
    public void testGetUserBookingsPastState() {
        User user = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        when(bookingRepository.findBookingByBookerAndEndBeforeOrderByStartDesc(Mockito.any(),
                Mockito.any(LocalDateTime.class), Mockito.any())).thenReturn(Arrays.asList(booking, booking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getUserBookings(
                1L, "PAST", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetUserBookingsFutureState")
    public void testGetAllByBookerFutureState() {
        User user = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        when(bookingRepository.findBookingByBookerAndStartAfterOrderByStartDesc(Mockito.any(),
                Mockito.any(LocalDateTime.class), Mockito.any())).thenReturn(Arrays.asList(booking, booking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getUserBookings(
                1L, "FUTURE", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetUserBookingsUnknownState")
    public void testGetUserBookingsUnknownState() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(mockUser1));
        assertThrows(UnknownStateException.class, () -> {
            bookingServiceImpl.getUserBookings(1L, "INVALID_STATE", 0, 10);
        });
    }

    @Test
    @DisplayName("Тест testGetOwnerBookingsAllState")
    public void testGetOwnerBookingsAllState() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(mockUser1));
        when(bookingRepository.getAllBookingsForOwner(Mockito.any(), Mockito.any()))
                .thenReturn(Arrays.asList(mockBooking1, mockBooking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getOwnerBookings(
                1L, "ALL", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetOwnerBookingsWaitingState")
    public void testGetOwnerBookingsWaitingState() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(mockUser1));
        when(bookingRepository.getBookingsForOwnerByStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(Arrays.asList(mockBooking1, mockBooking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getOwnerBookings(
                1L, "WAITING", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetOwnerBookingsRejectedState")
    public void testGetOwnerBookingsRejectedState() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(mockUser1));
        when(bookingRepository.getBookingsForOwnerByStatus(Mockito.any(), Mockito.any(),
                Mockito.any())).thenReturn(Arrays.asList(mockBooking1, mockBooking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getOwnerBookings(
                1L, "REJECTED", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetOwnerBookingsCurrentState")
    public void testGetOwnerBookingsCurrentState() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(mockUser1));
        when(bookingRepository.getCurrentBookingForOwner(Mockito.any(), Mockito.any(
                        LocalDateTime.class), Mockito.any(LocalDateTime.class),
                Mockito.any())).thenReturn(Arrays.asList(mockBooking1, mockBooking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getOwnerBookings(
                1L, "CURRENT", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetOwnerBookingsPastState")
    public void testGetOwnerBookingsPastState() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(mockUser1));
        when(bookingRepository.getPastBookingForOwner(Mockito.any(), Mockito.any(LocalDateTime.class),
                Mockito.any())).thenReturn(Arrays.asList(mockBooking1, mockBooking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(mockBooking1), BookingMapper.bookingToResponse(mockBooking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getOwnerBookings(
                1L, "PAST", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetOwnerBookingsFutureCase")
    public void testGetOwnerBookingsFutureCase() {
        User user = mockUser1;
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Booking booking = mockBooking1;
        booking.setBooker(user);
        Booking booking2 = mockBooking2;
        booking2.setBooker(user);
        when(bookingRepository.getFutureBookingForOwner(Mockito.any(),
                Mockito.any(LocalDateTime.class), Mockito.any())).thenReturn(Arrays.asList(booking, booking2));
        List<BookingResponseDto> expectedResponse = Arrays.asList(
                BookingMapper.bookingToResponse(booking), BookingMapper.bookingToResponse(booking2));
        List<BookingResponseDto> actualResponse = bookingServiceImpl.getOwnerBookings(
                1L, "FUTURE", 0, 10);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Тест testGetOwnerBookingsDefaultCase")
    public void testGetOwnerBookingsDefaultCase() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(mockUser1));
        assertThrows(UnknownStateException.class, () -> {
            bookingServiceImpl.getOwnerBookings(1L, "INVALID_STATE", 0, 10);
        });
    }
}

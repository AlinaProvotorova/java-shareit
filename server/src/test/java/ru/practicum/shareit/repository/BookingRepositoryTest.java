package ru.practicum.shareit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(scripts = "/test/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
class BookingRepositoryTest {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final BookingRepository bookingRepository;

    @Test
    void findBookingByBookerOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<User> userOptional = userRepository.findById(1L);
        User user = userOptional.orElse(new User());
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(4L).get(), bookingRepository.findById(3L).get());
        List<Booking> bookingListActual = bookingRepository.findBookingByBookerOrderByStartDesc(user, pageable);
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void findBookingByBookerAndStatusOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<User> userOptional = userRepository.findById(1L);
        User user = userOptional.orElse(new User());
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(3L).get());
        List<Booking> bookingListActual = bookingRepository.findBookingByBookerAndStatusOrderByStartDesc(user, BookingStatus.APPROVED, pageable);
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void findBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<User> userOptional = userRepository.findById(2L);
        User user = userOptional.orElse(new User());
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(2L).get());
        List<Booking> bookingListActual = bookingRepository.findBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                user,
                LocalDateTime.of(2023, 8, 21, 16, 0),
                LocalDateTime.of(2023, 8, 21, 14, 0),
                pageable
        );
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void findBookingByBookerAndEndBeforeOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<User> userOptional = userRepository.findById(2L);
        User user = userOptional.orElse(new User());
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(1L).get());
        List<Booking> bookingListActual = bookingRepository.findBookingByBookerAndEndBeforeOrderByStartDesc(
                user,
                LocalDateTime.of(2023, 8, 21, 16, 0),
                pageable
        );
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void findBookingByBookerAndStartAfterOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<User> userOptional = userRepository.findById(2L);
        User user = userOptional.orElse(new User());
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(2L).get());
        List<Booking> bookingListActual = bookingRepository.findBookingByBookerAndStartAfterOrderByStartDesc(
                user,
                LocalDateTime.of(2023, 8, 21, 12, 0),
                pageable
        );
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void getBookingsForOwnerByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<User> userOptional = userRepository.findById(1L);
        User user = userOptional.orElse(new User());
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(1L).get());
        List<Booking> bookingListActual = bookingRepository.getBookingsForOwnerByStatus(user.getId(), BookingStatus.APPROVED, pageable);
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void getAllBookingsForOwner() {
        Pageable pageable = PageRequest.of(0, 10);
        Optional<User> userOptional = userRepository.findById(1L);
        User user = userOptional.orElse(new User());
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(2L).get(), bookingRepository.findById(1L).get());
        List<Booking> bookingListActual = bookingRepository.getAllBookingsForOwner(user.getId(), pageable);
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void getCurrentBookingForOwner() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(2L).get());
        List<Booking> bookingListActual = bookingRepository.getCurrentBookingForOwner(
                1L,
                LocalDateTime.of(2023, 8, 21, 16, 0),
                LocalDateTime.of(2023, 8, 21, 14, 0),
                pageable
        );
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void getPastBookingForOwner() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(1L).get());
        List<Booking> bookingListActual = bookingRepository.getPastBookingForOwner(
                1L,
                LocalDateTime.of(2023, 8, 21, 14, 0),
                pageable
        );
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void getFutureBookingForOwner() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(2L).get());
        List<Booking> bookingListActual = bookingRepository.getFutureBookingForOwner(
                1L,
                LocalDateTime.of(2023, 8, 20, 14, 0),
                pageable
        );
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void findAllByBooker_IdAndItem_IdAndEndBefore() {
        List<Booking> bookingListExpected = List.of(bookingRepository.findById(1L).get());
        List<Booking> bookingListActual = bookingRepository.findAllByBooker_IdAndItem_IdAndEndBefore(
                2L,
                1L,
                LocalDateTime.of(2023, 8, 20, 13, 0)
        );
        assertThat(bookingListActual).isEqualTo(bookingListExpected);
    }

    @Test
    void findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc() {
        Booking bookingExpected = bookingRepository.findById(3L).get();
        Booking bookingActual = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                2L,
                LocalDateTime.of(2023, 8, 22, 13, 0),
                BookingStatus.APPROVED
        );
        assertThat(bookingExpected).isEqualTo(bookingActual);
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc() {
        Booking bookingExpected = bookingRepository.findById(3L).get();
        Booking bookingActual = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                2L,
                LocalDateTime.of(2023, 8, 22, 8, 0),
                BookingStatus.APPROVED
        );
        assertThat(bookingExpected).isEqualTo(bookingActual);
    }
}
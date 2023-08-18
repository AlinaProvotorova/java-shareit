package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Constants.BOOKING_NOT_FOUND;
import static ru.practicum.shareit.utils.Constants.ITEM_NOT_FOUND;
import static ru.practicum.shareit.utils.Constants.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        Item item = itemRepository.findById(bookingRequest.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format(ITEM_NOT_FOUND, bookingRequest.getItemId()))
        );
        BookingValidate.validateBookingRequest(bookingRequest, item, user);
        Booking booking = BookingMapper.requestToBooking(bookingRequest);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        bookingRepository.save(booking);
        log.info("Создан Booking {} от User c ID {}.", booking, userId);
        return BookingMapper.bookingToResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto updateBookingStatus(Long bookingId, Long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId))
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        BookingValidate.validateBookingStatusUpdate(booking, user, approved);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.info("UserOwner c ID {} подтвердил (APPROVED) запрос на Booking с id = {} ", userId,
                    booking.getId());
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.info("UserOwner c ID {} отклонил (REJECTED) запрос на Booking с id = {} ", userId,
                    booking.getId());
        }
        return BookingMapper.bookingToResponse(booking);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId))
        );
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Данные о конкретном Booking (включая его статус) может видеть только User Owner или Booker.");
        }
        log.info("Возвращен запрос на бронирование с id = {} ", bookingId);
        return BookingMapper.bookingToResponse(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        List<Booking> bookList;
        switch (state) {
            case "ALL":
                bookList = bookingRepository.findBookingByBookerOrderByStartDesc(user);
                break;
            case "WAITING":
            case "REJECTED":
                bookList = bookingRepository.findBookingByBookerAndStatusOrderByStartDesc(user,
                        BookingStatus.valueOf(state));
                break;
            case "CURRENT":
                LocalDateTime dateTime = LocalDateTime.now();
                bookList = bookingRepository.findBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        dateTime, dateTime);
                break;
            case "PAST":
                LocalDateTime dateTime1 = LocalDateTime.now();
                bookList = bookingRepository.findBookingByBookerAndEndBeforeOrderByStartDesc(user, dateTime1);
                break;
            case "FUTURE":
                LocalDateTime dateTime2 = LocalDateTime.now();
                bookList = bookingRepository.findBookingByBookerAndStartAfterOrderByStartDesc(user, dateTime2);
                break;
            default:
                throw new UnknownStateException(state);
        }
        return bookList.stream().map(BookingMapper::bookingToResponse).collect(Collectors.toList());
    }


    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        List<Booking> bookList;
        switch (state) {
            case "ALL":
                bookList = bookingRepository.getAllBookingsForOwner(userId);
                break;
            case "WAITING":
            case "REJECTED":
                bookList = bookingRepository.getBookingsForOwnerByStatus(userId, BookingStatus.valueOf(state));
                break;
            case "CURRENT":
                LocalDateTime dateTime = LocalDateTime.now();
                bookList = bookingRepository.getCurrentBookingForOwner(userId, dateTime, dateTime);
                break;
            case "PAST":
                LocalDateTime dateTime1 = LocalDateTime.now();
                bookList = bookingRepository.getPastBookingForOwner(userId, dateTime1);
                break;
            case "FUTURE":
                LocalDateTime dateTime2 = LocalDateTime.now();
                bookList = bookingRepository.getFutureBookingForOwner(userId, dateTime2);
                break;
            default:
                throw new UnknownStateException(state);
        }
        return bookList.stream().map(BookingMapper::bookingToResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {

    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(
            @Valid @RequestBody BookingRequestDto bookingRequest,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return bookingService.createBooking(bookingRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
            @RequestParam boolean approved
    ) {
        return bookingService.updateBookingStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getUserBookings(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return bookingService.getOwnerBookings(userId, state);
    }

    @DeleteMapping("/{id}")
    public void deleteBookingById(@PathVariable Long id) {
        bookingService.deleteById(id);
    }
}

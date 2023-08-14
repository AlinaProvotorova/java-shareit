package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class BookingValidate {

    public static void validateBookingRequest(BookingRequestDto bookingRequest, Item item, User user) {
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item не доступен для бронирования");
        }
        if (bookingRequest.getStart().isAfter(bookingRequest.getEnd()) || bookingRequest.getStart()
                .equals(bookingRequest.getEnd())) {
            throw new IllegalArgumentException("Start time позде End time у Booking");
        }
        if (bookingRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата начала не может быть в прошлом");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("BookingStatus для данного Booking может установить только владелец");
        }
    }

    public static void validateBookingStatusUpdate(Booking booking, User user, boolean approved) {
        if (booking.getStatus().equals(BookingStatus.APPROVED) ||
                booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new IllegalArgumentException("Невозможно изменить статус. Статус бронирования уже изменен на APPROVED или REJECTED.");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalArgumentException("Невозможно изменить статус. Статус бронирования можно изменить только для Booking, в статусе WAITING.");
        }
        if (!booking.getItem().getOwner().getId().equals(user.getId()) ||
                booking.getItem().getOwner().getId().equals(booking.getBooker().getId())) {
            throw new NotFoundException("Статус бронирования может быть изменен только владельцем.");
        }
    }
}

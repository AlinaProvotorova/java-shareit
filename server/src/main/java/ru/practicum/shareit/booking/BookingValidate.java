package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public class BookingValidate {

    public void validateItemAndUserForBooking(Item item, User user) {
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item не доступен для бронирования");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("BookingStatus для данного Booking может установить только владелец");
        }
    }

    public void validateBookingStatusUpdate(Booking booking, User user) {
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

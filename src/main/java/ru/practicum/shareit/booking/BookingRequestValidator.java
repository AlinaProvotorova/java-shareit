package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingRequestValidator implements ConstraintValidator<ValidBookingRequest, BookingRequestDto> {
    @Override
    public void initialize(ValidBookingRequest constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingRequestDto bookingRequest, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingRequest.getStart() == null || bookingRequest.getEnd() == null ||
                bookingRequest.getStart().isAfter(bookingRequest.getEnd()) ||
                bookingRequest.getStart().equals(bookingRequest.getEnd())) {
            throw new IllegalArgumentException("Некорректное время бронирования");
        }
        return true;
    }
}

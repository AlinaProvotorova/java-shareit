package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDate;

@Data
public class BookingDto {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private BookingStatus status;
}


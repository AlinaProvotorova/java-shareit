package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class BookingResponseDto {
    private Long id;

    @NotNull(message = "Не указанно время начала бронирования.")
    @FutureOrPresent(message = "Время начала бронирования не должно быть в прошлом.")
    private LocalDateTime start;

    @NotNull(message = "Не указанно время окончания бронирования.")
    @Future(message = "Время окончания бронирования не должно быть в будущем.")
    private LocalDateTime end;

    private Long itemId;
    private Long bookerId;

    @Enumerated(EnumType.ORDINAL)
    private ItemDto item;

    private UserDto booker;
    private BookingStatus status;
}
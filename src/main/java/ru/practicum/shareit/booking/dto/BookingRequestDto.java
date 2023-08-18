package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.Constants.PATTERN_DATETIME;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingRequestDto {

    @NotNull
    private Long itemId;

    @NotNull(message = "Не указанно время начала бронирования.")
    @FutureOrPresent(message = "Время начала бронирования не должно быть в прошлом.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATETIME)
    private LocalDateTime start;

    @NotNull(message = "Не указанно время окончания бронирования.")
    @Future(message = "Время окончания бронирования не должно быть в будущем.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATETIME)
    private LocalDateTime end;
}
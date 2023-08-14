package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class BookingRequestDto {
    @NotNull
    private Long itemId;
    @NotNull(message = "Не указанно время начала бронирования.")
    @FutureOrPresent(message = "Время начала бронирования не должно быть в прошлом.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @NotNull(message = "Не указанно время окончания бронирования.")
    @Future(message = "Время окончания бронирования не должно быть в будущем.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
}
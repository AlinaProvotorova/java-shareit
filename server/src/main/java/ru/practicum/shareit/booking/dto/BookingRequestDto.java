package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.Constants.PATTERN_DATETIME;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BookingRequestDto {
    private Long itemId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATETIME)
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_DATETIME)
    private LocalDateTime end;
}
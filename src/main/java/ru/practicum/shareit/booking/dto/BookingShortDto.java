package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class BookingShortDto {

    @NotNull
    private Long id;

    @NotNull
    private Long bookerId;
}

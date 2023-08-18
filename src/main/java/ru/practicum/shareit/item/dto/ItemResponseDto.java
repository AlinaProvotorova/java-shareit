package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemResponseDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private Long requestId;
    private List<CommentResponseDto> comments;


    public static ItemResponseDto create(Booking lastBooking, Booking nextBooking, Item item, List<Comment> comments) {
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .nextBooking(BookingMapper.bookingToShort(nextBooking))
                .lastBooking(BookingMapper.bookingToShort(lastBooking))
                .name(item.getName())
                .id(item.getId())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(CommentMapper.listCommentsToListResponse(comments))
                .build();

        if (item.getRequest() != null) {
            itemResponseDto.setRequestId(item.getRequest().getId());
        }
        return itemResponseDto;
    }

}
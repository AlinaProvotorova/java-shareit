package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.coyote.Request;
import ru.practicum.shareit.user.User;

@Builder
@Getter
@Setter
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Request request;
}

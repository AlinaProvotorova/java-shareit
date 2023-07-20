package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User {
    Integer id;
    String name;
    String email;
}

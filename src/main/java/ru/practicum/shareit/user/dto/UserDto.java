package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Data
public class UserDto {
    Integer id;
    @NotBlank
    String name;
    @Email
    @NotBlank
    String email;
}

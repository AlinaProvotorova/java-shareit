package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$",
            message = "Email не корректный")
    @NotBlank
    private String email;
}

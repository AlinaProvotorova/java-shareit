package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @NotNull
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @NotNull
    @NotBlank(message = "адрес электронной почты не должен быть пустым")
    @Pattern(regexp = "^.+@.+\\..+$", message = "Некорректный адрес электронной почты")
    @Column(name = "email", length = 512, nullable = false, unique = true)
    private String email;
}

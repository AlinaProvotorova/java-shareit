package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@Table(name = "item_request")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_id")
    private User requester;
    private LocalDateTime created;
}

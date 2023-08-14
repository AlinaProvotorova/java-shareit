package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@Table(name = "bookings", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(name = "start_time")
    private LocalDateTime start;
    @NonNull
    @Column(name = "end_time")
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status")
    private BookingStatus status;
}



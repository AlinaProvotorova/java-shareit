package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerOrderByStartDesc(User user, Pageable pageable);

    List<Booking> findBookingByBookerAndStatusOrderByStartDesc(User user, BookingStatus state, Pageable pageable);

    List<Booking> findBookingByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime dateTime, LocalDateTime dateTime1, Pageable pageable);

    List<Booking> findBookingByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findBookingByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime dateTime, Pageable pageable);

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 AND B.status = ?2 order by B.start desc")
    List<Booking> getBookingsForOwnerByStatus(Long ownerId, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 ORDER BY B.start DESC")
    List<Booking> getAllBookingsForOwner(Long ownerId, Pageable pageable);

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 AND B.start < ?2 AND B.end > ?3 ORDER BY B.start DESC")
    List<Booking> getCurrentBookingForOwner(Long ownerId, LocalDateTime date1, LocalDateTime date2, Pageable pageable);

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 AND B.end < ?2 ORDER BY B.start DESC")
    List<Booking> getPastBookingForOwner(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query(value = "SELECT B FROM Booking B WHERE B.item.owner.id = ?1 AND B.start > ?2 ORDER BY B.start DESC")
    List<Booking> getFutureBookingForOwner(Long ownerId, LocalDateTime date, Pageable pageable);

    List<Booking> findAllByBooker_IdAndItem_IdAndEndBefore(Long userId, Long itemId, LocalDateTime localDateTime);

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
            long itemId, LocalDateTime end, BookingStatus bookingStatus);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
            long itemId, LocalDateTime start, BookingStatus bookingStatus);

}

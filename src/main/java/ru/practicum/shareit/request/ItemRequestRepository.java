package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester_idOrderByCreatedAsc(Long userId);

    Page<ItemRequest> findAllByRequester_IdNotIn(Collection<Long> ownerId, Pageable pageable);
}

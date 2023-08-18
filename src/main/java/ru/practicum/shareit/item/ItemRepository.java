package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select * " +
            "from ITEMS " +
            "where owner_id = ?1", nativeQuery = true)
    List<Item> findAllByOwnerId(Long userId, Pageable pageable);

    @Query("select it "
            + "from Item it "
            + "where it.available = true "
            + "and (lower (it.name) like concat('%', lower(?1), '%') "
            + "or lower (it.description) like concat('%', lower(?1), '%')) ")
    List<Item> findByText(String text, Pageable pageable);

    List<Item> findAllByRequestIdOrderByIdAsc(Long requestId);

}

package ru.practicum.shareit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(scripts = "/test/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    void findAllByOwnerId() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> itemListActual = itemRepository.findAllByOwnerId(1L, pageable);
        List<Item> itemListExpected = List.of(itemRepository.findById(1L).get());
        assertThat(itemListActual).isEqualTo(itemListExpected);
    }

    @Test
    void findByText() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> itemListActual = itemRepository.findByText("abc", pageable);
        List<Item> itemListExpected = List.of(itemRepository.findById(1L).get());
        assertThat(itemListActual).isEqualTo(itemListExpected);
    }

    @Test
    void findAllByRequestIdOrderByIdAsc() {
        List<Item> itemListActual = itemRepository.findAllByRequestIdOrderByIdAsc(1L);
        List<Item> itemListExpected = List.of(itemRepository.findById(1L).get());
        assertThat(itemListActual).isEqualTo(itemListExpected);
    }
}
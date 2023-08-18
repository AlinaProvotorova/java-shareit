package ru.practicum.shareit.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(scripts = "/test/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    void findAllByRequester_idOrderByCreatedAsc() {
        List<ItemRequest> itemListActual = itemRequestRepository.findAllByRequester_idOrderByCreatedAsc(1L);
        List<ItemRequest> itemListExpected = List.of(itemRequestRepository.findById(1L).get());
        assertThat(itemListActual).isEqualTo(itemListExpected);
    }

    @Test
    void findAllByRequester_IdNotIn() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> itemPage = itemRequestRepository.findAllByRequester_IdNotIn(List.of(1L), pageable);
        List<ItemRequest> itemListActual = itemPage.getContent();
        List<ItemRequest> itemListExpected = List.of(itemRequestRepository.findById(2L).get());
        assertThat(itemListActual).isEqualTo(itemListExpected);
    }
}
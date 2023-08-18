package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImp;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@DisplayName("Тесты класса ItemRequestService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private User mockUser1;
    private Item mockItem1;
    private Item mockItem2;
    private ItemRequest mockItemRequest1;
    private ItemRequest mockItemRequest2;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ItemRequestServiceImp itemRequestServiceImp;

    private MockitoSession session;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        itemRequestServiceImp = new ItemRequestServiceImp(itemRequestRepository, itemRepository, userRepository);
        mockUser1 = new User(1L, "Test1", "test@yandex.ru");
        User mockUser2 = new User(2L, "Test2", "test@yandex.ru");
        mockItem1 = Item.builder()
                .id(1L)
                .name("Test1")
                .description("test")
                .available(true)
                .owner(mockUser1)
                .build();
        mockItem2 = Item.builder()
                .id(2L)
                .name("Test2")
                .description("test")
                .available(true)
                .owner(mockUser2)
                .build();
        mockItemRequest1 = new ItemRequest(1L, "test", mockUser2, LocalDateTime.of(2021, 12, 12, 1, 1, 1));
        mockItemRequest2 = new ItemRequest(2L, "test", mockUser1, LocalDateTime.of(2021, 12, 12, 1, 1, 1));
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    @DisplayName("Тест на создание itemRequest")
    public void createTest() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToDto(mockItemRequest1);
        when(itemRequestRepository.save(any())).thenReturn(mockItemRequest1);
        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser1));
        ItemRequestDto itemRequestDto2 = itemRequestServiceImp.create(itemRequestDto, mockUser1.getId());
        verify(itemRepository, never()).save(mockItem1);
        assertNotNull(itemRequestDto2);
        assertEquals(itemRequestDto.getId(), itemRequestDto2.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequestDto2.getDescription());
    }

    @Test
    @DisplayName("Тест на создание itemRequest некорректным User")
    public void create_shouldThrowEntityNotFoundException_whenUserNotFound() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.itemRequestToDto(mockItemRequest1);
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestServiceImp.create(itemRequestDto, userId));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Тест на получение всех itemRequest для конкретного User requester")
    public void testGetAllForRequester() {
        User user = mockUser1;
        ItemRequest itemRequest = mockItemRequest1;
        itemRequest.setRequester(user);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequester_idOrderByCreatedAsc(Mockito.any())).thenReturn(itemRequests);
        when(itemRepository.findAllByRequestIdOrderByIdAsc(Mockito.any())).thenReturn(List.of());
        List<ItemRequestResponseDto> result = itemRequestServiceImp.getAllForRequester(user.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Тест на получение всех itemRequest конкретным User requester у которого нет запросов")
    public void testGetAllForRequesterEmptyList() {
        User user = mockUser1;
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequester_idOrderByCreatedAsc(user.getId())).thenReturn(List.of());
        List<ItemRequestResponseDto> result = itemRequestServiceImp.getAllForRequester(user.getId());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Тест на получение всех itemRequest")
    public void testGetAllRequests() {
        User user = mockUser1;
        ItemRequest itemRequest1 = mockItemRequest1;
        itemRequest1.setRequester(user);
        ItemRequest itemRequest2 = mockItemRequest2;
        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);
        Item item1 = mockItem1;
        Item item2 = mockItem2;
        List<Item> items = List.of(item1, item2);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequester_IdNotIn(List.of(user.getId()),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"))))
                .thenReturn(new PageImpl<>(itemRequests));
        when(itemRepository.findAllByRequestIdOrderByIdAsc(1L)).thenReturn(items);
        when(itemRepository.findAllByRequestIdOrderByIdAsc(2L)).thenReturn(new ArrayList<>());
        List<ItemRequestResponseDto> result = itemRequestServiceImp.getAllRequests(0, 10, user.getId());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    @DisplayName("Тест на получение itemRequest по Id")
    public void testGetById() {
        User user = mockUser1;
        ItemRequest itemRequest = mockItemRequest1;
        itemRequest.setRequester(user);
        List<Item> items = List.of(mockItem1, mockItem2);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestIdOrderByIdAsc(itemRequest.getId())).thenReturn(items);
        ItemRequestResponseDto result = itemRequestServiceImp.getById(itemRequest.getId(), user.getId());
        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(2, result.getItems().size());
    }

    @Test
    @DisplayName("Тест на получение itemRequest по Id некорректным User")
    public void testUserNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestServiceImp.getById(requestId, userId));
    }

    @Test
    @DisplayName("Тест на получение несуществующего itemRequest по Id")
    public void testItemRequestNotFoundException() {
        Long requestId = 1L;
        User user = mockUser1;
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemRequestServiceImp.getById(requestId, user.getId()));
    }

}
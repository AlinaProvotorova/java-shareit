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
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Transactional
@DisplayName("Тесты класса ItemService")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private User mockUser1;
    private User mockUser2;
    private Item mockItem1;
    private Item mockItem2;
    private Booking mockBooking1;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    private MockitoSession session;

    @BeforeEach
    void init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking();
        itemService = new ItemServiceImpl(itemRepository, itemRequestRepository, userRepository, bookingRepository, commentRepository);
        mockUser1 = new User(1L, "Test1", "test@yandex.ru");
        mockUser2 = new User(2L, "Test2", "test@yandex.ru");
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
        mockBooking1 = new Booking(1L, LocalDateTime.of(2021, 12, 12, 1, 1), LocalDateTime.of(2021, 12, 22, 1, 1), mockItem1, mockUser2, BookingStatus.APPROVED);
    }

    @AfterEach
    void tearDown() {
        session.finishMocking();
    }

    @Test
    @DisplayName("Тест на создание Item")
    public void saveNewItemTest() {
        ItemDto itemDto = ItemMapper.toItemDto(mockItem1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser1));
        when(itemRepository.save(Mockito.any())).thenReturn(ItemMapper.dtoToItem(itemDto));
        ItemDto itemDto2 = itemService.saveNewItem(mockUser1.getId(), itemDto);
        assertNotNull(itemDto2);
        assertEquals(itemDto.getName(), itemDto2.getName());
    }


    @Test
    @DisplayName("Тест на создание Item повторные тесты")
    public void testSaveNewItemItem_UserNotFound() {
        when(userRepository.findById(mockUser1.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            itemService.saveNewItem(mockUser1.getId(), ItemMapper.toItemDto(mockItem1));
        });
        verify(userRepository, Mockito.times(1)).findById(mockUser1.getId());
        verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Тест на обновление Item")
    public void updateItemTest() {
        ItemDto itemDto1 = ItemMapper.toItemDto(mockItem1);
        ItemDto itemDto2 = ItemMapper.toItemDto(mockItem2);
        User owner = mockUser1;

        when((userRepository.findById(Mockito.any()))).thenReturn(Optional.of(mockUser1));
        when(itemRepository.findById(itemDto1.getId())).thenReturn(Optional.of(mockItem1));
        when(itemRepository.saveAndFlush(any())).thenReturn(mockItem1);

        itemService.saveNewItem(owner.getId(), itemDto1);
        itemDto2.setId(1L);
        ItemDto result = itemService.updateItem(1L, owner.getId(), itemDto1);
        assertNotNull(result);
        assertEquals(itemDto1.getId(), result.getId());
        verify(itemRepository, times(1)).findById(itemDto1.getId());
        verify(itemRepository, times(1)).saveAndFlush(any());
    }

    @Test
    @DisplayName("Тест на обновление Item не User Owner")
    public void testUpdateItemWithWrongOwner() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setName("Old Name");
        item.setDescription("Old Description");
        item.setAvailable(false);
        item.setOwner(owner);
        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(1L, 2L, ItemMapper.toItemDto(mockItem1));
        });
    }

    @Test
    @DisplayName("Тест на получение Item по ID для User Owner")
    public void getItemByIdTest() {
        User owner = mockUser1;
        when(userRepository.save(Mockito.any())).thenReturn(owner);
        when((userRepository.findById(Mockito.any()))).thenReturn(Optional.of(mockUser1));
        when(itemRepository.save(Mockito.any())).thenReturn(mockItem1);
        when((itemRepository.findById(Mockito.any()))).thenReturn(Optional.ofNullable(mockItem1));
        userRepository.save(owner);
        ItemMapper.dtoToItem(itemService.saveNewItem(owner.getId(), ItemMapper.toItemDto(mockItem1)));

        assertEquals("Test1", itemService.getItemById(1L, 1L).getName());
        assertEquals(Long.valueOf(1), itemService.getItemById(1L, 1L).getId());
        assertEquals("test", itemService.getItemById(1L, 1L).getDescription());
        assertEquals(true, itemService.getItemById(1L, 1L).getAvailable());
    }

    @Test
    @DisplayName("Тест на получение Item по ID для User Не Owner")
    public void testGetByIdForUser() {
        Item item = mockItem1;
        User owner = mockUser1;
        User commentator = mockUser2;

        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Comment 1")
                .item(item)
                .author(commentator)
                .build();
        Comment comment2 = Comment.builder()
                .id(2L)
                .text("Comment 2")
                .item(item)
                .author(commentator)
                .build();

        List<Comment> comments = List.of(comment1, comment2);
        when(userRepository.save(owner)).thenReturn(owner);
        when(userRepository.save(commentator)).thenReturn(commentator);
        when((userRepository.findById(1L))).thenReturn(Optional.of(mockUser1));
        when(itemRepository.save(Mockito.any())).thenReturn(item);
        when((itemRepository.findById(Mockito.any()))).thenReturn(Optional.ofNullable(item));

        userRepository.save(owner);
        userRepository.save(commentator);
        itemService.saveNewItem(owner.getId(), ItemMapper.toItemDto(item));
        ItemResponseDto itemResponseDto = ItemResponseDto.create(null, null, item, comments);
        itemResponseDto.setComments(CommentMapper.listCommentsToListResponse(comments));

        ItemResponseDto itemResponseDto2 = itemService.getItemById(item.getId(), commentator.getId());

        assertEquals(item.getId(), itemResponseDto2.getId());
        assertEquals(item.getName(), itemResponseDto2.getName());
        assertEquals(item.getDescription(), itemResponseDto2.getDescription());
        assertEquals(item.getAvailable(), itemResponseDto2.getAvailable());
        assertEquals(CommentMapper.listCommentsToListResponse(comments), itemResponseDto.getComments());
    }

    @Test
    @DisplayName("Тест на удаление Item по ID от User Owner")
    public void deleteItemTest() {
        when((userRepository.findById(1L))).thenReturn(Optional.of(mockUser1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem1));
        itemService.getItemById(1L, 1L);
        assertEquals(mockUser1.getId(), 1L);

        itemService.deleteItem(1L, 1L);

        verify(itemRepository, Mockito.times(1)).deleteById(1L);
        verify(itemRepository, Mockito.times(2)).findById(Mockito.any());
        verify(itemRepository, Mockito.never()).save(Mockito.any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Тест на удаление Item по ID от User НЕ Owner")
    public void testDeleteItem_UserNotOwner() {
        User owner = new User();
        owner.setId(3L);
        Item item = new Item();
        item.setOwner(owner);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> {
            itemService.deleteItem(1L, 2L);
        });
    }

    @Test
    @DisplayName("Тест на поиск Item по text")
    public void testSearchBy() {
        User user = mockUser1;
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findByText("text", PageRequest.of(0, 10)))
                .thenReturn(List.of(mockItem1, mockItem2));
        List<ItemDto> result = itemService.searchBy("text", user.getId(), 0, 10);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Тест на поиск Item с пустым текстовым запросом")
    public void testSearchByEmptyText() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<ItemDto> result = itemService.searchBy(null, 1L, 0, 10);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Тест на добавление Comment к Item")
    public void addCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .text("comment")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem1));
        when(bookingRepository.findAllByBooker_IdAndItem_IdAndEndBefore(
                Mockito.any(), Mockito.any(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockBooking1));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CommentResponseDto result = itemService.addComment(commentDto, 1L, 1L);
        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
        assertEquals(mockUser1.getId(), 1L);
        assertEquals(mockItem1.getId(), 1L);
        verify(userRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(bookingRepository).findAllByBooker_IdAndItem_IdAndEndBefore(
                Mockito.any(), Mockito.any(), Mockito.any(LocalDateTime.class));
        verify(commentRepository).save(any(Comment.class));
    }
}




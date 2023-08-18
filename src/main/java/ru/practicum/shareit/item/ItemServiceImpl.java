package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Constants.ITEM_NOT_FOUND;
import static ru.practicum.shareit.utils.Constants.ITEM_REQUEST_NOT_FOUND;
import static ru.practicum.shareit.utils.Constants.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getAllItems() {
        log.info("Получен список всех существующих Item.");
        return itemRepository.findAll().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemResponseDto createItemResponse(Item item) {
        List<Comment> comments = commentRepository.findAllByItem_Id(item.getId());
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                item.getId(), LocalDateTime.now(), BookingStatus.APPROVED);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                item.getId(), LocalDateTime.now(), BookingStatus.APPROVED);
        return ItemResponseDto.create(lastBooking, nextBooking, item, comments);
    }

    @Override
    public List<ItemResponseDto> getOwnersItems(int from, int size, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        Pageable pageable = PageRequest.of(from == 0 ? 0 : (from / size), size);
        List<ItemResponseDto> itemResponseDto = itemRepository.findAllByOwnerId(userId, pageable)
                .stream()
                .map(this::createItemResponse)
                .collect(Collectors.toList());

        log.info("Получен список всех существующих Item для пользователя c ID {}.", userId);
        return itemResponseDto;
    }

    @Override
    public List<ItemDto> searchBy(String text, Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        if (text == null || text.trim().isEmpty()) {
            log.info("Получен пустой лист поиска по запросу User ID {}.", userId);
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(from == 0 ? 0 : (from / size), size);
        log.info("Выполнен поиск по Item с текстом {}", text);
        return itemRepository.findByText(text, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItemById(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(ITEM_NOT_FOUND, id))
        );
        List<Comment> comments = commentRepository.findAllByItem_Id(id);
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(item.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(),
                LocalDateTime.now(), BookingStatus.APPROVED);
        if (item.getOwner().getId().equals(userId)) {
            ItemResponseDto itemResponseDto = ItemResponseDto.create(lastBooking, nextBooking, item, comments);
            log.info("Item с ID {} получена для User Owner с ID {}.", id, userId);
            return itemResponseDto;
        }
        ItemResponseDto itemResponseDto = ItemMapper.toResponseItem(item);
        itemResponseDto.setComments(CommentMapper.listCommentsToListResponse(comments));
        log.info("Item с ID {} получена для User с ID {}.", id, userId);
        return itemResponseDto;
    }

    @Override
    @Transactional
    public ItemDto saveNewItem(Long userId, ItemDto item) {
        ItemValidate.checkItemAvailable(item);
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        Item newItem = ItemMapper.dtoToItem(item);
        newItem.setOwner(owner);
        Long requestId = item.getRequestId();
        if (requestId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                    () -> new NotFoundException(String.format(ITEM_REQUEST_NOT_FOUND, requestId))
            );
            newItem.setRequest(itemRequest);
        }
        itemRepository.save(newItem);
        log.info("Пользователь с ID {} создал Item c ID {}.", userId, newItem.getId());
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format(ITEM_NOT_FOUND, itemId))
        );
        ItemValidate.checkOwnerItem(item, userId);
        Item updater = itemRepository.saveAndFlush(ItemMapper.dtoToItem(itemDto, item));
        log.info("User(Owner) c ID {} обновил данные Item c ID {}.", userId, itemId);
        return ItemMapper.toItemDto(updater);
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(ITEM_NOT_FOUND, id))
        );
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        ItemValidate.checkOwnerItem(item, userId);
        log.info("Item с ID {} удален.", id);
        itemRepository.deleteById(id);
    }

    @Transactional
    @Override
    public CommentResponseDto addComment(CommentDto commentDto, long itemId, long userId) {
        if (bookingRepository.findAllByBooker_IdAndItem_IdAndEndBefore(userId, itemId, LocalDateTime.now()).isEmpty()) {
            throw new IllegalArgumentException("Оставлять Comment может только User, у которого есть завершённый Booking для данного Item");
        }

        Comment comment = CommentMapper.dtoToComment(commentDto);
        comment.setAuthor(
                userRepository.findById(userId).orElseThrow(
                        () -> new NotFoundException(String.format(USER_NOT_FOUND, userId)))
        );
        comment.setItem(
                itemRepository.findById(itemId).orElseThrow(
                        () -> new NotFoundException(String.format(ITEM_NOT_FOUND, itemId)))
        );
        comment = commentRepository.save(comment);
        log.info("Добавлен Comment для Item c id = {} от User с id = {}",
                comment.getItem().getId(), comment.getAuthor().getId());
        return CommentMapper.toResponseDto(comment);
    }
}

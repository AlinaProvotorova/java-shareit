package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Constants.ITEM_REQUEST_NOT_FOUND;
import static ru.practicum.shareit.utils.Constants.USER_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestResponseDto create(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, userId)));
        ItemRequest itemRequest = ItemRequestMapper.dtoToItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        log.info("itemRequestDto {} сохранён.", itemRequestDto);

        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestResponse(itemRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getAllForRequester(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_idOrderByCreatedAsc(userId);
        if (requests.isEmpty()) {
            log.info("Получен пустой список ItemRequest для User c ID {} - у него нет запросов.", userId);
            return List.of();
        }
        List<ItemRequestResponseDto> itemResponseDtos = processItemRequests(requests, userId);

        log.info("Получен список ItemRequest вместе с данными об ответах на них для User c ID {}.", userId);
        return itemResponseDtos;
    }


    @Override
    public List<ItemRequestResponseDto> getAllRequests(int from, int size, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequester_IdNotIn(
                List.of(userId), pageable).getContent();

        List<ItemRequestResponseDto> itemResponseDtos = processItemRequests(requests, userId);
        log.info("Получен полный список ItemRequest по запросу от User c ID {}.", userId);
        return itemResponseDtos;
    }

    @Override
    public ItemRequestResponseDto getById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format(USER_NOT_FOUND, userId))
        );
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format(ITEM_REQUEST_NOT_FOUND, requestId))
        );
        List<ItemResponseDto> items = ItemMapper.listItemsToListResponseDto(
                itemRepository.findAllByRequestIdOrderByIdAsc(requestId));

        log.info("Получен ItemRequest с ID {} по запросу от User c ID {}.", requestId, userId);
        return ItemRequestMapper.listItemResponseToItemRequestResponse(itemRequest, items);
    }

    private List<ItemRequestResponseDto> processItemRequests(List<ItemRequest> requests, Long userId) {
        if (requests.isEmpty()) {
            log.info("Получен пустой список ItemRequest для User c ID {} - у него нет запросов.", userId);
            return List.of();
        }

        Map<Long, List<Item>> itemsByRequestId = itemRepository.findByRequestIn(requests)
                .stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId(), Collectors.toList()));

        return requests.stream()
                .map(a -> ItemRequestMapper.listItemResponseToItemRequestResponse(a, ItemMapper.listItemsToListResponseDto(
                        itemsByRequestId.getOrDefault(a.getId(), Collections.emptyList()))))
                .collect(Collectors.toList());
    }

}

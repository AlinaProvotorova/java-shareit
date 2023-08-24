package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto create(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestResponseDto> getAllForRequester(Long userId);

    List<ItemRequestResponseDto> getAllRequests(int from, int size, long userId);

    ItemRequestResponseDto getById(Long requestId, Long userId);

}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.utils.Constants;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto create(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllForRequester(
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId) {
        return itemRequestService.getAllForRequester(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAll(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId) {
        return itemRequestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@PathVariable Long requestId,
                                          @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId) {
        return itemRequestService.getById(requestId, userId);
    }
}

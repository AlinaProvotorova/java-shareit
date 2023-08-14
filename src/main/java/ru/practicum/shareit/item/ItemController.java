package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/all")
    public List<ItemDto> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping
    public List<ItemResponseDto> getOwnersItems(@RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId) {
        return itemService.getOwnersItems(userId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemById(
            @PathVariable @Positive Long id,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchBy(
            @RequestParam(value = "text") @NotBlank String text,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return itemService.searchBy(text, userId);
    }

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                               @Valid @RequestBody ItemDto item) {
        return itemService.saveNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable @Positive Long itemId,
                              @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(itemId, userId, item);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(
            @PathVariable @Positive Long id,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        itemService.deleteItem(id, userId);
        return String.format("Вещь для бронирования с ID %d удалена", id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable("itemId") @Positive long itemId,
                                         @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
                                         @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(commentDto, itemId, userId);
    }
}

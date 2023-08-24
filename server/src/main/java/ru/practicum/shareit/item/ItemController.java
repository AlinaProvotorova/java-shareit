package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.utils.Constants;

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
    public List<ItemResponseDto> getOwnersItems(
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        return itemService.getOwnersItems(from, size, userId);
    }

    @GetMapping("/{id}")
    public ItemResponseDto getItemById(
            @PathVariable Long id,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchBy(
            @RequestParam(value = "text") String text,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
            @RequestParam(defaultValue = "0", required = false) int from,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        return itemService.searchBy(text, userId, from, size);
    }

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                               @RequestBody ItemDto item) {
        return itemService.saveNewItem(userId, item);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(itemId, userId, item);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(
            @PathVariable Long id,
            @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        itemService.deleteItem(id, userId);
        return String.format("Вещь для бронирования с ID %d удалена", id);
    }


    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable("itemId") long itemId,
                                         @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                                         @RequestBody CommentDto commentDto) {
        return itemService.addComment(commentDto, itemId, userId);
    }
}

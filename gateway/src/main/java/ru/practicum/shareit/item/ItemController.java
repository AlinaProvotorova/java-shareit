package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Constants;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItems() {
        return itemClient.getAllItems();
    }

    @GetMapping
    public ResponseEntity<Object> getOwnersItems(@RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                                                 @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10", required = false) @Positive int size
    ) {
        return itemClient.getOwnersItems(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long id,
                                              @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return itemClient.getItemById(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchBy(@RequestParam(value = "text") String text,
                                           @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                                           @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10", required = false) @Positive int size
    ) {
        return itemClient.searchBy(text, userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewItem(@RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                                              @Validated(Marker.OnCreate.class) @RequestBody ItemDto item) {
        return itemClient.saveNewItem(userId, item);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable @Positive Long itemId,
                                             @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId,
                                             @Validated(Marker.OnUpdate.class) @RequestBody ItemDto item) {
        return itemClient.updateItem(itemId, userId, item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable @Positive Long id,
                                             @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Long userId
    ) {
        return itemClient.deleteItem(id, userId);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") @Positive Long itemId,
                                             @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) @Positive Long userId,
                                             @Validated(Marker.OnCreate.class) @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}

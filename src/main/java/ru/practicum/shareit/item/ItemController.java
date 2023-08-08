package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public List<ItemDto> getOwnersItems(@RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Integer userId) {
        return itemService.getOwnersItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable @Positive Integer id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchBy(@RequestParam(value = "text") @NotBlank String text) {
        return itemService.searchBy(text);
    }

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Integer userId,
                               @Valid @RequestBody ItemDto item) {
        return itemService.saveNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable @Positive Integer itemId,
                              @RequestHeader(value = Constants.HEADER_USER_ID_VALUE) Integer userId,
                              @RequestBody ItemDto item) {
        return itemService.updateItem(itemId, userId, item);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable @Positive Integer id) {
        itemService.deleteItem(id);
        return String.format("Вещь для бронирования с ID %d удалена", id);
    }
}

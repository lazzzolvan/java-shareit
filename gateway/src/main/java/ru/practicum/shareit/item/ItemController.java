package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(header) Long userId, @Valid @RequestBody ItemRequest itemRequest) {
        log.info("Creating item {}", itemRequest);
        return itemClient.create(userId, itemRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable Long itemId,
                                      @RequestHeader(header) Long userId) {
        log.info("Получен запрос на поиск вещи с id = {}", itemId);
        return itemClient.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(header) Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение списка вещей владельца с id = {}", userId);
        return itemClient.getAllByUser(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> remove(@RequestHeader(header) Long userId,
                                         @PathVariable Long itemId) {
        log.info("Remove item by id {}", itemId);
        return itemClient.remove(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(header) Long userId,
                                         @PathVariable Long itemId, @RequestBody ItemRequest itemRequest) {
        log.info("Update item {} by id {}", itemRequest, itemId);
        return itemClient.update(userId, itemId, itemRequest);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(header) Long userId, @RequestParam("text") String name,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Search item with name {}", name);
        return itemClient.searchItem(name, from, size, userId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentRequest commentRequest,
                                                @PathVariable Long itemId,
                                                @RequestHeader(header) Long userId) {
        log.info("Добавляем отзыва от пользователя с id = {}", userId);
        return itemClient.createComment(userId, itemId, commentRequest);
    }

}

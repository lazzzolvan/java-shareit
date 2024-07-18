package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.dto.CommentRequest;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final ItemService service;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemResponse create(@RequestHeader(header) Long userId, @RequestBody ItemRequest itemRequest) {
        log.info("Creating item {}", itemRequest);
        return service.create(itemRequest, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse get(@PathVariable Long itemId,
                            @RequestHeader(header) Long userId) {
        log.info("Получен запрос на поиск вещи с id = {}", itemId);
        return service.get(itemId, userId);
    }

    @GetMapping
    public List<ItemResponse> getAll(@RequestHeader(header) Long userId,
                                     @RequestParam(name = "from") Integer from,
                                     @RequestParam(name = "size") Integer size) {
        log.info("Получен запрос на получение списка вещей владельца с id = {}", userId);
        return service.getAllByUser(userId, from, size);
    }

    @DeleteMapping("/{itemId}")
    public Boolean remove(@PathVariable Long itemId) {
        log.info("Remove item by id {}", itemId);
        return service.remove(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestHeader(header) Long userId,
                               @PathVariable Long itemId, @RequestBody ItemRequest itemRequest) {
        log.info("Update item {} by id {}", itemRequest, itemId);
        return service.update(userId, itemId, itemRequest);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchItem(@RequestParam("text") String name,
                                         @RequestParam(name = "from") Integer from,
                                         @RequestParam(name = "size") Integer size) {
        log.info("Search item with name {}", name);
        return service.searchItem(name, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentResponse createComment(@RequestBody CommentRequest commentRequest,
                                         @PathVariable Long itemId,
                                         @RequestHeader(header) Long userId) {
        log.info("Добавляем отзыва от пользователя с id = {}", userId);
        return service.createComment(userId, itemId, commentRequest);
    }

}

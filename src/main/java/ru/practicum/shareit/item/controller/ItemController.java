package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ItemResponse create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequest itemRequest) {
        log.info("Creating item {}", itemRequest);
        return service.create(itemRequest, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponse get(@PathVariable Long itemId) {
        log.info("Get item by id {}", itemId);
        return service.get(itemId);
    }

    @GetMapping
    public List<ItemResponse> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items by user {} id", userId);
        return service.getAllByUser(userId);
    }

    @DeleteMapping("/{itemId}")
    public Boolean remove(@PathVariable Long itemId) {
        log.info("Remove item by id {}", itemId);
        return service.remove(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId, @RequestBody ItemRequest itemRequest) {
        log.info("Update item {} by id {}", itemRequest, itemId);
        return service.update(userId, itemId, itemRequest);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchItem(@RequestParam("text") String name) {
        log.info("Search item with name {}", name);
        return service.searchItem(name);
    }

}

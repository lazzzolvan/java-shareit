package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {

    private final ItemService service;
    private final ItemMapper mapper;

    @PostMapping
    public ItemResponse create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequest itemRequest) {
        Item item = mapper.toItem(itemRequest);
        Item itemModified = service.create(item, userId);
        log.info("Creating item {}", item);
        return mapper.toItemResponse(itemModified);
    }

    @GetMapping("/{itemId}")
    public ItemResponse get(@PathVariable Long itemId) {
        log.info("Get item by id {}", itemId);
        return mapper.toItemResponse(service.get(itemId));
    }

    @GetMapping
    public List<ItemResponse> getAll() {
        log.info("Get all items");
        List<Item> items = service.getAll();
        return mapper.toItemResponseOfList(items);
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
        Item item = mapper.toItem(itemRequest);
        Item itemUpdate = service.update(userId, itemId, item);
        return mapper.toItemResponse(itemUpdate);
    }

    @GetMapping("/search?text={text}")
    public ItemResponse searchItem(@PathVariable("text") String name) {
        log.info("Search item with name {}", name);
        return mapper.toItemResponse(service.searchItem(name));
    }
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse create(ItemRequest item, Long userId);

    ItemResponse update(Long userId, Long itemId, ItemRequest item);

    Boolean remove(Long itemId);

    List<ItemResponse> getAllByUser(Long userId);

    ItemResponse get(Long itemId);

    List<ItemResponse> searchItem(String name);
}

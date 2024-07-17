package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.controller.dto.CommentRequest;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse create(ItemRequest item, Long userId);

    ItemResponse update(Long userId, Long itemId, ItemRequest item);

    Boolean remove(Long itemId);

    List<ItemResponse> getAllByUser(Long userId, Integer from, Integer size);

    ItemResponse get(Long itemId, Long userId);

    List<ItemResponse> searchItem(String name, Integer from, Integer size);

    CommentResponse createComment(Long userId, Long itemId, CommentRequest commentRequest);
}

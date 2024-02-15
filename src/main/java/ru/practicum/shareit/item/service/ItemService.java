package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Item item, Long userId);

    Item update(Long userId, Long itemId, Item item);

    Boolean remove(Long itemId);

    List<Item> getAllByUser(Long userId);

    Item get(Long itemId);

    List<Item> searchItem(String name);
}

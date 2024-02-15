package ru.practicum.shareit.item.storage.memory;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item, Long userId);

    Item update(Long userId, Long itemId, Item item);

    Boolean remove(Long itemId);

    List<Item> getAll();

    Item get(Long itemId);
}

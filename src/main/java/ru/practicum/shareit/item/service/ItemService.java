package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Item item, Long userId);

    Item update(Long userId, Long itemId, Item item);

    Boolean remove(Long itemId);

    List<Item> getAll();

    Item get(Long itemId);

    Item searchItem(String name);
}

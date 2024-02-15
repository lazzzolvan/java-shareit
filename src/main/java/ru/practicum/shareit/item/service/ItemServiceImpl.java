package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.memory.ItemStorage;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;

    @Override
    public Item create(Item item, Long userId) {
        return storage.create(item, userId);
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        return storage.update(userId, itemId, item);
    }

    @Override
    public Boolean remove(Long itemId) {
        return storage.remove(itemId);
    }

    @Override
    public List<Item> getAllByUser(Long userId) {
        return storage.getAllByUser(userId);
    }

    @Override
    public Item get(Long itemId) {
        return storage.get(itemId);
    }

    public List<Item> searchItem(String name) {
        List<Item> items = new ArrayList<>();
        if (name.isEmpty())
            return items;
        for (Item item : storage.getAll()) {
            if (item.getDescription().toLowerCase().contains(name.toLowerCase()) && item.getAvailable())
                items.add(item);
        }
        return items;
    }
}

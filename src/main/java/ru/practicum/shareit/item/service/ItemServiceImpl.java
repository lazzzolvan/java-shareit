package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.memory.ItemStorage;

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
    public List<Item> getAll() {
        return storage.getAll();
    }

    @Override
    public Item get(Long itemId) {
        return storage.get(itemId);
    }

    public Item searchItem(String name) {
        for (Item item : storage.getAll()) {
            if (item.getName().toLowerCase().contains(name.toLowerCase()))
                return item;
        }
        throw new DataNotFoundException(String.format("Item not found, name: %s", name.toLowerCase()));
    }
}

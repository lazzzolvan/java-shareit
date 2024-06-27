package ru.practicum.shareit.item.storage.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.memory.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> itemStorage = new HashMap<>();
    private final UserStorage userStorage;
    private Long generateID = 0L;


    @Override
    public Item create(Item item, Long userId) {
        userStorage.get(userId);
        item.setId(++generateID);
        item.setOwner(userStorage.get(userId));
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        if (!itemStorage.get(itemId).getOwner().equals(userId) || !itemStorage.containsKey(itemId))
            throw new DataNotFoundException(String.format("Item %s id not found or not found user id %s", itemId, userId));
        if (item.getName() != null) {
            itemStorage.get(itemId).setName(item.getName());
        }
        if (item.getAvailable() != null) {
            itemStorage.get(itemId).setAvailable(item.getAvailable());
        }
        if (item.getDescription() != null) {
            itemStorage.get(itemId).setDescription(item.getDescription());
        }
        return itemStorage.get(itemId);
    }

    @Override
    public Boolean remove(Long itemId) {
        if (!itemStorage.containsKey(itemId))
            throw new DataNotFoundException(String.format("Item %s id not found", itemId));
        itemStorage.remove(itemId);
        return true;
    }

    @Override
    public List<Item> getAllByUser(Long userId) {
        List<Item> items = new ArrayList<>();
        for (Item value : itemStorage.values()) {
            if (value.getOwner().equals(userId))
                items.add(value);
        }
        return items;
    }

    @Override
    public Item get(Long itemId) {
        if (!itemStorage.containsKey(itemId))
            throw new DataNotFoundException(String.format("Item %s id not found", itemId));
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(itemStorage.values());
    }
}

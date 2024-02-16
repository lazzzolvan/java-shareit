package ru.practicum.shareit.item.storage.memory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.memory.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> itemStorage = new HashMap<>();
    private final UserStorage userStorage;
    private Long generateID = 0L;

    public InMemoryItemStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Item create(Item item, Long userId) {
        userStorage.get(userId);
        Item itemModified = Item.builder()
                .id(++generateID)
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .request(item.getRequest())
                .owner(userId)
                .build();
        itemStorage.put(itemModified.getId(), itemModified);
        return itemModified;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        if (itemStorage.get(itemId).getOwner().equals(userId) || !itemStorage.containsKey(itemId))
            throw new DataNotFoundException(String.format("Item %s id not found or not found user id %s", itemId, userId));
        Item itemUpdate = Item.builder()
                .id(itemId)
                .owner(itemStorage.get(itemId).getOwner())
                .build();
        if (item.getName() != null) {
            itemUpdate.setName(item.getName());
        } else {
            itemUpdate.setName(itemStorage.get(itemId).getName());
        }
        if (item.getAvailable() != null) {
            itemUpdate.setAvailable(item.getAvailable());
        } else {
            itemUpdate.setAvailable(itemStorage.get(itemId).getAvailable());
        }
        if (item.getDescription() != null) {
            itemUpdate.setDescription(item.getDescription());
        } else {
            itemUpdate.setDescription(itemStorage.get(itemId).getDescription());
        }
        itemStorage.put(itemId, itemUpdate);
        return itemUpdate;
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

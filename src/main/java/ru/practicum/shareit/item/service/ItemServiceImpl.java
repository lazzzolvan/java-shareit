package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.memory.ItemStorage;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final ItemMapper mapper;

    @Override
    public ItemResponse create(ItemRequest item, Long userId) {
        Item itemModified = mapper.toItem(item);
        return mapper.toItemResponse(storage.create(itemModified, userId));
    }

    @Override
    public ItemResponse update(Long userId, Long itemId, ItemRequest item) {
        Item itemModified = mapper.toItem(item);
        return mapper.toItemResponse(storage.update(userId, itemId, itemModified));
    }

    @Override
    public Boolean remove(Long itemId) {
        return storage.remove(itemId);
    }

    @Override
    public List<ItemResponse> getAllByUser(Long userId) {
        return mapper.toItemResponseOfList(storage.getAllByUser(userId));
    }

    @Override
    public ItemResponse get(Long itemId) {
        return mapper.toItemResponse(storage.get(itemId));
    }

    public List<ItemResponse> searchItem(String name) {
        List<Item> items = new ArrayList<>();
        if (name.isEmpty())
            return mapper.toItemResponseOfList(items);
        for (Item item : storage.getAll()) {
            if (item.getDescription().toLowerCase().contains(name.toLowerCase()) && item.getAvailable())
                items.add(item);
        }
        return mapper.toItemResponseOfList(items);
    }
}

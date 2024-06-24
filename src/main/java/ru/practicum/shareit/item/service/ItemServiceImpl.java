package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.storage.memory.ItemStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    //private final ItemStorage storage;
    private final ItemRepository repository;
    private final ItemMapper mapper;

    @Override
    @Transactional
    public ItemResponse create(ItemRequest item, Long userId) {
        Item itemModified = mapper.toItem(item);
        return mapper.toItemResponse(repository.save(itemModified));
    }

    @Override
    @Transactional
    public ItemResponse update(Long userId, Long itemId, ItemRequest item) {
        Item itemModified = repository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Item with %s id not found", itemId)));

        if (!itemModified.getOwner().equals(userId))
            throw new DataNotFoundException(String.format("User id %s not found for item id %s", userId, itemId));

        itemModified.setName(Objects.requireNonNull(itemModified.getName(), item.getName()));
        itemModified.setAvailable(Objects.requireNonNullElse(itemModified.getAvailable(), item.getAvailable()));
        itemModified.setDescription(Objects.requireNonNull(itemModified.getDescription(), item.getDescription()));
        return mapper.toItemResponse(repository.save(itemModified));
    }

    @Override
    public Boolean remove(Long itemId) {
        repository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Item with %s id not found", itemId)));
        repository.deleteById(itemId);
        return true;
    }

    @Override
    public List<ItemResponse> getAllByUser(Long userId) {
        List<Item> items = new ArrayList<>();
        for (Item item : repository.findAll()) {
            if (item.getOwner().equals(userId))
                items.add(item);
        }
        return mapper.toItemResponseOfList(items);
    }

    @Override
    public ItemResponse get(Long itemId) {
        return mapper.toItemResponse(repository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Item with %s id not found", itemId))));
    }

    public List<ItemResponse> searchItem(String name) {
        List<Item> items = new ArrayList<>();
        if (name.isEmpty())
            return mapper.toItemResponseOfList(items);
        for (Item item : repository.findAll()) {
            if (item.getDescription().toLowerCase().contains(name.toLowerCase()) && item.getAvailable())
                items.add(item);
        }
        return mapper.toItemResponseOfList(items);
    }
}

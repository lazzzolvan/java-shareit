package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper
public interface ItemMapper {

    Item toItem(ItemRequest itemRequest);

    ItemResponse toItemResponse(Item item);

    List<ItemResponse> toItemResponseOfList(List<Item> itemList);
}

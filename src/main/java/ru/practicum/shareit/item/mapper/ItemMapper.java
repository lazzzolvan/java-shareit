package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "itemRequest.id", target = "id")
    @Mapping(source = "itemRequest.name", target = "name")
    @Mapping(source = "user", target = "owner")
    @Mapping(source = "itemRequest.requestId", target = "request.id")
    Item toItem(ItemRequest itemRequest, User user);

    @Mapping(source = "item.request.id", target = "requestId")
    ItemResponse toItemResponse(Item item);

    List<ItemResponse> toItemResponseOfList(List<Item> itemList);
}

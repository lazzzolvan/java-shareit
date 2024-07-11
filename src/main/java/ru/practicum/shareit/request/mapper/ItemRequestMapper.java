package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.dto.ItemRequestDto;
import ru.practicum.shareit.request.controller.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(source = "itemRequestDto.id", target = "id")
    @Mapping(source = "itemRequestDto.userId", target = "requester.id")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(source = "requester.id", target = "userId")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    List<ItemRequestDto> toItemRequestList(List<ItemRequest> itemRequests);

/*    @Mapping(source = "items.id", target = "items.id")
    @Mapping(source = "items.name", target = "items.name")
    @Mapping(source = "items.description", target = "items.description")
    @Mapping(source = "items.available", target = "items.available")
    @Mapping(source = "items.owner", target = "items.owner")
    @Mapping(source = "items.request.id", target = "items.request")*/
/*    @Mapping(source = "items", target = "items")*/
    @Mapping(source = "itemRequests.requester.id", target = "userId")
    @Mapping(source = "items", target = "items.")
    ItemRequestDtoWithItems toItemRequestDtoWithItems(ItemRequest itemRequests, List<Item> items);
}

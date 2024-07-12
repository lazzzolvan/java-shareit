package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
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

    @Mapping(source = "itemRequests.requester.id", target = "userId")
    @Mapping(target = "items", qualifiedByName = "toItemResponse")
    ItemRequestDtoWithItems toItemRequestDtoWithItems(ItemRequest itemRequests, List<Item> items);

    @Named("toItemResponse")
    default ItemResponse toItemResponse(Item item) {
        if (item == null) {
            return null;
        }

        Long requestId = itemRequestId(item);
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        Long id = item.getId();

        BookingShortDto nextBooking = null;
        BookingShortDto lastBooking = null;
        List<CommentResponse> comments = null;

        return new ItemResponse(id, name, description, available, requestId, nextBooking, lastBooking, comments);
    }

    default Long itemRequestId(Item item) {
        return item != null && item.getRequest() != null ? item.getRequest().getId() : null;
    }
}

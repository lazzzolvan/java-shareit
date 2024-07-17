package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.dto.ItemRequestDto;
import ru.practicum.shareit.request.controller.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperTest {

    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    void setUp() {
        itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    }

    @Test
    void toItemRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test Description")
                .userId(1L)
                .creationDate(LocalDateTime.now())
                .build();

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);

        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequestDto.getUserId(), itemRequest.getRequester().getId());
        assertEquals(itemRequestDto.getCreationDate(), itemRequest.getCreationDate());
    }

    @Test
    void toItemRequestNull() {
        ItemRequestDto itemRequestDto = null;

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);

        assertEquals(null, itemRequest);
    }

    @Test
    void toItemRequestDto() {
        User requester = new User();
        requester.setId(1L);

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requester(requester)
                .creationDate(LocalDateTime.now())
                .build();

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getRequester().getId(), itemRequestDto.getUserId());
        assertEquals(itemRequest.getCreationDate(), itemRequestDto.getCreationDate());
    }

    @Test
    void toItemRequestList() {
        User requester = new User();
        requester.setId(1L);

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requester(requester)
                .creationDate(LocalDateTime.now())
                .build();

        List<ItemRequest> itemRequestList = Collections.singletonList(itemRequest);
        List<ItemRequestDto> itemRequestDtoList = itemRequestMapper.toItemRequestList(itemRequestList);

        assertEquals(1, itemRequestDtoList.size());
        assertEquals(itemRequest.getId(), itemRequestDtoList.get(0).getId());
        assertEquals(itemRequest.getDescription(), itemRequestDtoList.get(0).getDescription());
        assertEquals(itemRequest.getRequester().getId(), itemRequestDtoList.get(0).getUserId());
        assertEquals(itemRequest.getCreationDate(), itemRequestDtoList.get(0).getCreationDate());
    }

    @Test
    void toItemRequestDtoWithItems() {
        User requester = new User();
        requester.setId(1L);

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requester(requester)
                .creationDate(LocalDateTime.now())
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Item Description")
                .available(true)
                .request(itemRequest)
                .build();

        List<Item> items = Collections.singletonList(item);
        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestMapper.toItemRequestDtoWithItems(itemRequest, items);

        assertEquals(itemRequest.getId(), itemRequestDtoWithItems.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDtoWithItems.getDescription());
        assertEquals(itemRequest.getRequester().getId(), itemRequestDtoWithItems.getUserId());
        assertEquals(itemRequest.getCreationDate(), itemRequestDtoWithItems.getCreationDate());
        assertEquals(1, itemRequestDtoWithItems.getItems().size());

        ItemResponse itemResponse = itemRequestDtoWithItems.getItems().get(0);
        assertEquals(item.getId(), itemResponse.getId());
        assertEquals(item.getName(), itemResponse.getName());
        assertEquals(item.getDescription(), itemResponse.getDescription());
        assertEquals(item.getAvailable(), itemResponse.getAvailable());
        assertEquals(item.getRequest().getId(), itemResponse.getRequestId());
        assertNull(itemResponse.getNextBooking());
        assertNull(itemResponse.getLastBooking());
        assertNull(itemResponse.getComments());
    }
}

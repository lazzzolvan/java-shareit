package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @InjectMocks
    private ItemMapperImpl itemMapper;

    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private ItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Item description")
                .available(true)
                .owner(user)
                .request(null)
                .build();

        itemRequest = new ItemRequest(
                1L,
                "Test Item",
                "Item description",
                true,
                user.getId(),
                null,
                null,
                null,
                Collections.emptyList()
        );

        itemResponse = new ItemResponse(
                1L,
                "Test Item",
                "Item description",
                true,
                null,
                null,
                null,
                Collections.emptyList()
        );
    }

    @Test
    void testToItem() {
        Item mappedItem = itemMapper.toItem(itemRequest, user);

        assertEquals(itemRequest.getId(), mappedItem.getId());
        assertEquals(itemRequest.getName(), mappedItem.getName());
        assertEquals(itemRequest.getDescription(), mappedItem.getDescription());
        assertEquals(itemRequest.getAvailable(), mappedItem.getAvailable());
        assertEquals(user, mappedItem.getOwner());
        assertEquals(null, mappedItem.getRequest().getId());
        assertEquals(null, mappedItem.getRequest().getDescription());
        assertEquals(null, mappedItem.getRequest().getCreationDate());
        assertEquals(null, mappedItem.getRequest().getRequester());
    }

    @Test
    void testToItemNull() {
        Item mappedItem = itemMapper.toItem(null, null);

        assertEquals(null, mappedItem);
    }

    @Test
    void testToItemResponse() {
        ItemResponse mappedItemResponse = itemMapper.toItemResponse(item);

        assertEquals(item.getId(), mappedItemResponse.getId());
        assertEquals(item.getName(), mappedItemResponse.getName());
        assertEquals(item.getDescription(), mappedItemResponse.getDescription());
        assertEquals(item.getAvailable(), mappedItemResponse.getAvailable());
        assertEquals(item.getRequest() != null ? item.getRequest().getId() : null, mappedItemResponse.getRequestId());
    }

    @Test
    void testToItemResponseNull() {
        ItemResponse mappedItemResponse = itemMapper.toItemResponse(null);

        assertEquals(null, mappedItemResponse);
    }

    @Test
    void testToItemResponseOfList() {
        List<Item> items = Collections.singletonList(item);

        List<ItemResponse> mappedItemResponses = itemMapper.toItemResponseOfList(items);

        assertEquals(items.size(), mappedItemResponses.size());

        ItemResponse mappedItemResponse = mappedItemResponses.get(0);
        assertEquals(item.getId(), mappedItemResponse.getId());
        assertEquals(item.getName(), mappedItemResponse.getName());
        assertEquals(item.getDescription(), mappedItemResponse.getDescription());
        assertEquals(item.getAvailable(), mappedItemResponse.getAvailable());
        assertEquals(item.getRequest() != null ? item.getRequest().getId() : null, mappedItemResponse.getRequestId());
    }

    @Test
    void testToItemResponseOfListNull() {
        List<ItemResponse> mappedItemResponses = itemMapper.toItemResponseOfList(null);

        assertEquals(null, mappedItemResponses);
    }
}

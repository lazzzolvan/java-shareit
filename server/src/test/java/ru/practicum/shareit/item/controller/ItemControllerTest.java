package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void testCreateItem() throws Exception {
        // Arrange
        Long userId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(userId)
                .requestId(2L)
                .build();

        ItemResponse response = ItemResponse.builder()
                .id(itemRequest.getId())
                .name(itemRequest.getName())
                .build();

        doReturn(response).when(itemService).create(any(ItemRequest.class), anyLong());

        // Act & Assert
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(itemRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.name").value(itemRequest.getName()));
    }

    @Test
    void testGetItem() throws Exception {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;

        ItemResponse response = ItemResponse.builder()
                .id(itemId)
                .name("Test Item")
                .build();

        doReturn(response).when(itemService).get(itemId, userId);

        // Act & Assert
        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void testRemoveItem() throws Exception {
        // Arrange
        Long itemId = 1L;

        doReturn(true).when(itemService).remove(itemId);

        // Act & Assert
        mockMvc.perform(delete("/items/{itemId}", itemId))
                .andExpect(status().isOk());
    }
}

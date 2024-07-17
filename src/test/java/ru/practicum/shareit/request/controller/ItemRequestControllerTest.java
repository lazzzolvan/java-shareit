package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.controller.dto.ItemRequestDto;
import ru.practicum.shareit.request.controller.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemRequestService requestService;

    @InjectMocks
    private ItemRequestController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCreateItemRequest() throws Exception {
        // Arrange
        long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test item request");

        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setDescription(requestDto.getDescription());

        doReturn(responseDto).when(requestService).createItemRequest(anyLong(), any(ItemRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void testGetItemRequestById() throws Exception {
        // Arrange
        long userId = 1L;
        long requestId = 1L;

        ItemRequestDtoWithItems responseDto = new ItemRequestDtoWithItems();
        responseDto.setId(requestId);

        doReturn(responseDto).when(requestService).getItemRequestById(anyLong(), anyLong());

        // Act & Assert
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(requestId));
    }

    @Test
    void testGetItemRequestsByOwnerId() throws Exception {
        // Arrange
        long userId = 1L;

        ItemRequestDtoWithItems responseDto = new ItemRequestDtoWithItems();

        doReturn(Collections.singletonList(responseDto)).when(requestService).getItemRequestsByRequestorId(anyLong());

        // Act & Assert
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testGetItemRequests() throws Exception {
        // Arrange
        long userId = 1L;
        int from = 0;
        int size = 10;

        ItemRequestDtoWithItems responseDto = new ItemRequestDtoWithItems();

        doReturn(Collections.singletonList(responseDto)).when(requestService).getItemRequests(anyLong(), eq(from), eq(size));

        // Act & Assert
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }
}

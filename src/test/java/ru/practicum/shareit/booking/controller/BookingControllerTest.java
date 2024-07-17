package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;


    @Test
    void testUpdateBooking() throws Exception {
        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(Item.builder().id(1L).build())
                .booker(User.builder().id(1L).build())
                .status(BookingStatus.APPROVED)
                .build();

        doReturn(response).when(bookingService).update(anyLong(), anyLong(), anyBoolean());

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(1L));
    }

    @Test
    void testGetById() throws Exception {
        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(Item.builder().id(1L).build())
                .booker(User.builder().id(1L).build())
                .status(BookingStatus.WAITING)
                .build();

        doReturn(response).when(bookingService).getById(anyLong(), anyLong());

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(1L));
    }
}

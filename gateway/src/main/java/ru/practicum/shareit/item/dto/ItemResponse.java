package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ItemResponse {

    private final Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
    private BookingShortDto nextBooking;
    private BookingShortDto lastBooking;
    private List<ru.practicum.shareit.item.controller.dto.CommentResponse> comments;
}

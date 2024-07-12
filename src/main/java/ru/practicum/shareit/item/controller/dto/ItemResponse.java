package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemResponse {

    private final Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
    private BookingShortDto nextBooking;
    private BookingShortDto lastBooking;
    private List<CommentResponse> comments;
}

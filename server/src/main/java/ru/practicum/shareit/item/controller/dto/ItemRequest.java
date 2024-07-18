package ru.practicum.shareit.item.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ItemRequest {

    private final Long id;

    private final String name;

    private final String description;

    private final Boolean available;

    private final Long owner;

    @JsonProperty("requestId")
    private final Long requestId;
    private final BookingShortDto nextBooking;
    private final BookingShortDto lastBooking;
    private final List<CommentRequest> comments;
}

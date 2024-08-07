package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.controller.dto.CommentRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ItemRequest {

    private final Long id;

    @NotNull
    @NotBlank
    private final String name;

    @NotBlank
    @Size(max = 200, min = 1)
    private final String description;

    @NotNull
    private final Boolean available;

    private final Long owner;

    @JsonProperty("requestId")
    private final Long requestId;
    private final BookingShortDto nextBooking;
    private final BookingShortDto lastBooking;
    private final List<CommentRequest> comments;
}

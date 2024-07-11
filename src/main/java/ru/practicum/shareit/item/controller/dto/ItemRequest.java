package ru.practicum.shareit.item.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@AllArgsConstructor
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

    private final User owner;

    @JsonProperty("requestId")
    private final Long requestId;
    private final BookingShortDto nextBooking;
    private final BookingShortDto lastBooking;
    private final List<CommentRequest> comments;
}

package ru.practicum.shareit.booking.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BookingResponse {

    private final Long id;

    private final LocalDateTime start;

    private final LocalDateTime end;

    private final Item item;

    private final User booker;

    private final Status status;
}

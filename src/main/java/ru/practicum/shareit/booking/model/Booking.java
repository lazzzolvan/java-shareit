package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Booking {

    private final Long id;

    private final LocalDateTime start;

    private final LocalDateTime end;

    private final Long item;

    private final Long booker;

    private final Status status;
}

package ru.practicum.shareit.booking.controller.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingShortDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;
    private Long bookerId;
}


package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingResponse create(BookingShortDto booking, Long userId);

    BookingResponse update(Long bookingId, Long userId, Boolean approved);

    BookingResponse getById(Long bookingId, Long userId);

    List<BookingResponse> getAllByUser(Long userId, BookingState state, Integer from, Integer size);

    List<BookingResponse> getAllByOwner(Long userId, BookingState state, Integer from, Integer size);
}

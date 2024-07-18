package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    private final String header = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid BookingShortDto bookingDto,
                                 @RequestHeader(header) Long userId) {
        log.info("Добавление отзыва от пользователя с id = {}", userId);
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable Long bookingId,
                                         @RequestHeader(header) Long userId,
                                         @RequestParam Boolean approved) {
        log.info("Обновление статуса бронирования id " + bookingId + ", пользователем id: " + userId);
        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(header) Long userId,
                                   @PathVariable Long bookingId) {
        log.info("Получаем информацию о бронировании: {}", bookingId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(header) Long userId,
                                              @RequestParam(name = "state",
                                                      required = false,
                                                      defaultValue = "ALL") BookingState state,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получаем все бронирования текущего пользователяс id = {}", userId);
        return bookingClient.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(header) Long userId,
                                               @RequestParam(name = "state",
                                                       required = false,
                                                       defaultValue = "ALL") BookingState state,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получаем все бронирования текущего владельца id = {}", userId);
        return bookingClient.getAllByOwner(userId, state, from, size);
    }
}



package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private final String header = "X-Sharer-User-Id";


    @PostMapping
    public BookingResponse create(@RequestBody @Valid BookingShortDto bookingDto,
                                  @RequestHeader(header) Long userId) {
        log.info("Добавление отзыва от пользователя с id = {}", userId);
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse updateBooking(@PathVariable Long bookingId,
                                    @RequestHeader(header) Long userId,
                                    @RequestParam Boolean approved) {
        log.info("Обновление статуса бронирования id " + bookingId + ", пользователем id: " + userId);
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getById(@RequestHeader(header) Long userId,
                              @PathVariable Long bookingId) {
        log.info("Получаем информацию о бронировании: {}", bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> getAllByUser(@RequestHeader(header) Long userId,
                                              @RequestParam(name = "state",
                                                 required = false,
                                                 defaultValue = "ALL") BookingState state) {
        log.info("Получаем все бронирования текущего пользователяс id = {}", userId);
        return bookingService.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public  List<BookingResponse> getAllByOwner(@RequestHeader(header) Long userId,
                                           @RequestParam(name = "state",
                                                   required = false,
                                                   defaultValue = "ALL") BookingState state) {
        log.info("Получаем все бронирования текущего владельца id = {}", userId);
        return bookingService.getAllByOwner(userId, state);
    }
}



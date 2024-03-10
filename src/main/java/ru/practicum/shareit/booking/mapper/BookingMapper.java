package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.controller.dto.BookingRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBook(BookingRequest bookingRequest);

    BookingResponse toBookingResponse(Booking booking);
}

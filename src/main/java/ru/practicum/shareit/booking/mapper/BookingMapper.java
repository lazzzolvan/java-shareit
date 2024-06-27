package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.booking.controller.dto.BookingRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Mapper(componentModel = "spring")
public interface BookingMapper {
    List<BookingResponse> toBookingResponseOfList(List<Booking> bookings);

    Booking toBook(BookingRequest bookingRequest);

    @Mappings({
            @Mapping(source = "booking.id", target = "id"),
            @Mapping(source = "booking.start", target = "start"),
            @Mapping(source = "booking.end", target = "end"),
            @Mapping(source = "item", target = "item"),
            @Mapping(source = "booker", target = "booker"),
            @Mapping(source = "booking.status", target = "status")
    })
    BookingResponse toBookingResponse(Booking booking, User booker, Item item);


    Booking toBookFromShort(BookingShortDto bookingShortDto);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "booker.id", target = "bookerId")
    BookingShortDto toBookingShortDtoFromBooking(Booking booking);

}

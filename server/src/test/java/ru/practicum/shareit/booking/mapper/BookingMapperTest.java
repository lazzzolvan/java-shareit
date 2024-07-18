package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.controller.dto.BookingRequest;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    @InjectMocks
    private BookingMapperImpl bookingMapper;

    private User user;
    private Item item;
    private Booking booking;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;
    private BookingShortDto bookingShortDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        bookingRequest = new BookingRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                BookingStatus.WAITING
        );

        bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .bookerId(user.getId())
                .build();
    }

    @Test
    void testToBookingResponseOfList() {
        List<Booking> bookings = Collections.singletonList(booking);

        List<BookingResponse> bookingResponses = bookingMapper.toBookingResponseOfList(bookings);

        assertEquals(bookings.size(), bookingResponses.size());
        BookingResponse mappedBookingResponse = bookingResponses.get(0);
        assertEquals(booking.getId(), mappedBookingResponse.getId());
        assertEquals(booking.getStart(), mappedBookingResponse.getStart());
        assertEquals(booking.getEnd(), mappedBookingResponse.getEnd());
        assertEquals(booking.getItem(), mappedBookingResponse.getItem());
        assertEquals(booking.getBooker(), mappedBookingResponse.getBooker());
        assertEquals(booking.getStatus(), mappedBookingResponse.getStatus());
    }

    @Test
    void testToBookingResponseOfListNull() {
        List<Booking> bookings = null;

        List<BookingResponse> bookingResponses = bookingMapper.toBookingResponseOfList(bookings);

        assertEquals(null, bookingResponses);
    }

    @Test
    void testToBook() {
        Booking mappedBooking = bookingMapper.toBook(bookingRequest);

        assertEquals(bookingRequest.getId(), mappedBooking.getId());
        assertEquals(bookingRequest.getStart(), mappedBooking.getStart());
        assertEquals(bookingRequest.getEnd(), mappedBooking.getEnd());
        assertEquals(bookingRequest.getItem(), mappedBooking.getItem());
        assertEquals(bookingRequest.getBooker(), mappedBooking.getBooker());
        assertEquals(bookingRequest.getBookingStatus(), mappedBooking.getStatus());
    }

    @Test
    void testToBookNull() {
        BookingRequest bookingRequestNull = null;
        Booking mappedBooking = bookingMapper.toBook(bookingRequestNull);

        assertEquals(null, mappedBooking);
    }

    @Test
    void testToBookingResponse() {
        BookingResponse mappedBookingResponse = bookingMapper.toBookingResponse(booking, user, item);

        assertEquals(booking.getId(), mappedBookingResponse.getId());
        assertEquals(booking.getStart(), mappedBookingResponse.getStart());
        assertEquals(booking.getEnd(), mappedBookingResponse.getEnd());
        assertEquals(booking.getItem(), mappedBookingResponse.getItem());
        assertEquals(booking.getBooker(), mappedBookingResponse.getBooker());
        assertEquals(booking.getStatus(), mappedBookingResponse.getStatus());
    }

    @Test
    void testToBookingResponseNull() {
        BookingResponse mappedBookingResponse = bookingMapper.toBookingResponse(null, null, null);

        assertEquals(null, mappedBookingResponse);
    }

    @Test
    void testToBookFromShort() {
        Booking mappedBooking = bookingMapper.toBookFromShort(bookingShortDto);

        assertEquals(bookingShortDto.getId(), mappedBooking.getId());
        assertEquals(bookingShortDto.getStart(), mappedBooking.getStart());
        assertEquals(bookingShortDto.getEnd(), mappedBooking.getEnd());
    }

    @Test
    void testToBookFromShortNull() {
        Booking mappedBooking = bookingMapper.toBookFromShort(null);

        assertEquals(null, mappedBooking);
    }

    @Test
    void testToBookingShortDtoFromBooking() {
        BookingShortDto mappedBookingShortDto = bookingMapper.toBookingShortDtoFromBooking(booking);

        assertEquals(booking.getId(), mappedBookingShortDto.getId());
        assertEquals(booking.getStart(), mappedBookingShortDto.getStart());
        assertEquals(booking.getEnd(), mappedBookingShortDto.getEnd());
        assertEquals(booking.getItem().getId(), mappedBookingShortDto.getItemId());
        assertEquals(booking.getBooker().getId(), mappedBookingShortDto.getBookerId());
    }
}

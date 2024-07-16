package ru.practicum.shareit.booking.controller.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingRequestTest {

    @Autowired
    private JacksonTester<BookingRequest> json;

    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus bookingStatus;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2023, 1, 1, 10, 0);
        end = LocalDateTime.of(2023, 1, 1, 12, 0);
        item = new Item(1L, "Test Item", "Description", true, null, null);
        booker = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        bookingStatus = BookingStatus.WAITING;
    }

    @Test
    void testSerializeBookingRequest() throws Exception {
        // Arrange
        BookingRequest bookingRequest = BookingRequest.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .bookingStatus(bookingStatus)
                .build();

        // Act
        var result = json.write(bookingRequest);

        // Assert
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingRequest.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-01T12:00:00");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(bookingRequest.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(bookingRequest.getItem().getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo(bookingRequest.getItem().getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(bookingRequest.getItem().getAvailable());
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(bookingRequest.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo(bookingRequest.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(bookingRequest.getBooker().getEmail());
        assertThat(result).extractingJsonPathStringValue("$.bookingStatus").isEqualTo("WAITING");
    }



    @Test
    void testDeserializeBookingRequest() throws Exception {
        // JSON representation of a BookingRequest
        String jsonContent = "{\"start\":\"2023-01-01T10:00:00\",\"end\":\"2023-01-01T12:00:00\",\"item\":{\"id\":1,\"name\":\"Test Item\",\"description\":\"Description\",\"available\":true},\"booker\":{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"},\"bookingStatus\":\"WAITING\"}";

        // Deserialize JSON to BookingRequest object
        BookingRequest request = json.parse(jsonContent).getObject();

        // Verify object fields
        assertThat(request.getStart()).isEqualTo(start);
        assertThat(request.getEnd()).isEqualTo(end);
        assertThat(request.getItem()).isEqualToComparingFieldByField(item);
        assertThat(request.getBooker()).isEqualToComparingFieldByField(booker);
        assertThat(request.getBookingStatus()).isEqualTo(bookingStatus);
    }
}

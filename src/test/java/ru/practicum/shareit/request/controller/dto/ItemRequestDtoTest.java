package ru.practicum.shareit.request.controller.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private LocalDateTime creationDate;

    @BeforeEach
    void setUp() {
        creationDate = LocalDateTime.of(2023, 1, 1, 10, 0);
    }

    @Test
    void testSerializeItemRequestDto() throws Exception {
        // Arrange
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Need to have a hammer")
                .userId(1L)
                .creationDate(creationDate)
                .build();

        // Act
        var result = json.write(dto);

        // Assert
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requestor");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(dto.getUserId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-01T10:00:00");
    }

    @Test
    void testDeserializeItemRequestDto() throws Exception {
        // Arrange
        String jsonContent = "{\"id\":1,\"description\":\"Need to have a hammer\",\"requestor\":1,\"created\":\"2023-01-01T10:00:00\"}";

        // Act
        var dto = json.parseObject(jsonContent);

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need to have a hammer");
        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getCreationDate()).isEqualTo(creationDate);
    }
}

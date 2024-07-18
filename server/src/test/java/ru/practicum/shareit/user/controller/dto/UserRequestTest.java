package ru.practicum.shareit.user.controller.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserRequestTest {

    @Autowired
    private JacksonTester<UserRequest> json;

    @Test
    void testSerialize() throws Exception {
        var userRequest = UserRequest.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        var result = json.write(userRequest);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userRequest.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userRequest.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userRequest.getEmail());
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"id\": 1,"
                + "\"name\": \"John Doe\","
                + "\"email\": \"john.doe@example.com\""
                + "}";

        var userRequest = json.parse(content).getObject();

        assertThat(userRequest.getId()).isEqualTo(1L);
        assertThat(userRequest.getName()).isEqualTo("John Doe");
        assertThat(userRequest.getEmail()).isEqualTo("john.doe@example.com");
    }
}

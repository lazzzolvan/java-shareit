package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.controller.dto.UserRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    private UserRequest userRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(1L, "Test User", "test@example.com");

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        userResponse = new UserResponse(1L, "Test User", "test@example.com");
    }

    @Test
    void testToUser() {
        User mappedUser = userMapper.toUser(userRequest);

        assertEquals(userRequest.getId(), mappedUser.getId());
        assertEquals(userRequest.getName(), mappedUser.getName());
        assertEquals(userRequest.getEmail(), mappedUser.getEmail());
    }

    @Test
    void testToUserResponse() {
        UserResponse mappedUserResponse = userMapper.toUserResponse(user);

        assertEquals(user.getId(), mappedUserResponse.getId());
        assertEquals(user.getName(), mappedUserResponse.getName());
        assertEquals(user.getEmail(), mappedUserResponse.getEmail());
    }

    @Test
    void testToUserResponseList() {
        List<User> users = Arrays.asList(user);

        List<UserResponse> mappedUserResponses = userMapper.toUserResponseList(users);

        assertEquals(users.size(), mappedUserResponses.size());

        UserResponse mappedUserResponse = mappedUserResponses.get(0);
        assertEquals(user.getId(), mappedUserResponse.getId());
        assertEquals(user.getName(), mappedUserResponse.getName());
        assertEquals(user.getEmail(), mappedUserResponse.getEmail());
    }
}

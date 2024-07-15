package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.controller.dto.UserRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        UserRequest request = new UserRequest(null, "John Doe", "john.doe@example.com");
        User user = User.builder().id(1L).name("John Doe").email("john.doe@example.com").build();
        UserResponse expectedResponse = new UserResponse(1L, "John Doe", "john.doe@example.com");

        when(mapper.toUser(any(UserRequest.class))).thenReturn(user);
        when(repository.save(any(User.class))).thenReturn(user);
        when(mapper.toUserResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse response = userService.create(request);

        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.getEmail(), response.getEmail());

        verify(mapper, times(1)).toUser(request);
        verify(repository, times(1)).save(user);
        verify(mapper, times(1)).toUserResponse(user);
    }

    @Test
    void testUpdateUserName() {
        Long userId = 1L;
        UserRequest request = new UserRequest(null, "Updated Name", null);
        User existingUser = User.builder().id(userId).name("John Doe").email("john.doe@example.com").build();
        User modifiedUser = User.builder().id(userId).name("Updated Name").email("john.doe@example.com").build();
        UserResponse expectedResponse = new UserResponse(userId, "Updated Name", "john.doe@example.com");

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.save(any(User.class))).thenReturn(modifiedUser);
        when(mapper.toUserResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse response = userService.update(userId, request);

        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.getEmail(), response.getEmail());

        verify(repository, times(1)).findById(userId);
        verify(repository, times(1)).save(any(User.class));
        verify(mapper, times(1)).toUserResponse(any(User.class));
    }

    @Test
    void testUpdateUserEmail() {
        Long userId = 1L;
        UserRequest request = new UserRequest(null, null, "updated.email@example.com");
        User existingUser = User.builder().id(userId).name("John Doe").email("john.doe@example.com").build();
        User modifiedUser = User.builder().id(userId).name("John Doe").email("updated.email@example.com").build();
        UserResponse expectedResponse = new UserResponse(userId, "John Doe", "updated.email@example.com");

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.save(any(User.class))).thenReturn(modifiedUser);
        when(mapper.toUserResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse response = userService.update(userId, request);

        assertNotNull(response);
        assertEquals(expectedResponse.getId(), response.getId());
        assertEquals(expectedResponse.getName(), response.getName());
        assertEquals(expectedResponse.getEmail(), response.getEmail());

        verify(repository, times(1)).findById(userId);
        verify(repository, times(1)).save(any(User.class));
        verify(mapper, times(1)).toUserResponse(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        Long userId = 1L;
        UserRequest request = new UserRequest(null, "Updated Name", "updated.email@example.com");

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.update(userId, request));

        verify(repository, times(1)).findById(userId);
        verify(repository, never()).save(any(User.class));
        verify(mapper, never()).toUserResponse(any(User.class));
    }

    @Test
    void testRemoveUser() {
        Long userId = 1L;
        User existingUser = User.builder().id(userId).name("John Doe").email("john.doe@example.com").build();

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> userService.remove(userId));

        verify(repository, times(1)).findById(userId);
        verify(repository, times(1)).deleteById(userId);
    }

    @Test
    void testRemoveUserNotFound() {
        Long userId = 1L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.remove(userId));

        verify(repository, times(1)).findById(userId);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void testGetAllUsers() {
        User user1 = User.builder().id(1L).name("John Doe").email("john.doe@example.com").build();
        User user2 = User.builder().id(2L).name("Jane Smith").email("jane.smith@example.com").build();
        UserResponse userResponse1 = new UserResponse(1L, "John Doe", "john.doe@example.com");
        UserResponse userResponse2 = new UserResponse(2L, "Jane Smith", "jane.smith@example.com");

        when(repository.findAll()).thenReturn(List.of(user1, user2));
        when(mapper.toUserResponseList(List.of(user1, user2))).thenReturn(List.of(userResponse1, userResponse2));

        List<UserResponse> responses = userService.getAll();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(userResponse1.getId(), responses.get(0).getId());
        assertEquals(userResponse1.getName(), responses.get(0).getName());
        assertEquals(userResponse1.getEmail(), responses.get(0).getEmail());
        assertEquals(userResponse2.getId(), responses.get(1).getId());
        assertEquals(userResponse2.getName(), responses.get(1).getName());
        assertEquals(userResponse2.getEmail(), responses.get(1).getEmail());

        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toUserResponseList(List.of(user1, user2));
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;
        User user = User.builder().id(userId).name("John Doe").email("john.doe@example.com").build();
        UserResponse userResponse = new UserResponse(userId, "John Doe", "john.doe@example.com");

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse response = userService.get(userId);

        assertNotNull(response);
        assertEquals(userResponse.getId(), response.getId());
        assertEquals(userResponse.getName(), response.getName());
        assertEquals(userResponse.getEmail(), response.getEmail());

        verify(repository, times(1)).findById(userId);
        verify(mapper, times(1)).toUserResponse(user);
    }

    @Test
    void testGetUserByIdNotFound() {
        Long userId = 1L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userService.get(userId));

        verify(repository, times(1)).findById(userId);
        verify(mapper, never()).toUserResponse(any(User.class));
    }
}

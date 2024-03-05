package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.controller.dto.UserRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest user);

    UserResponse update(Long userId, UserRequest user);

    Boolean remove(Long userId);

    List<UserResponse> getAll();

    UserResponse get(Long id);
}

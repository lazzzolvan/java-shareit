package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.controller.dto.UserRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest userRequest) {
        User user = mapper.toUser(userRequest);
        User userModified = service.create(user);
        log.info("Creating user {}", userModified);
        return mapper.toUserResponse(userModified);
    }

    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable Long userId) {
        log.info("Get user by id {}", userId);
        User user = service.get(userId);
        return mapper.toUserResponse(user);
    }

    @GetMapping
    public List<UserResponse> get() {
        log.info("Get all users ");
        List<User> users = service.getAll();
        return mapper.toUserResponseList(users);
    }

    @DeleteMapping("/{userId}")
    public Boolean remove(@PathVariable Long userId) {
        log.info("Remove user by id {}", userId);
        return service.remove(userId);
    }

    @PatchMapping("{userId}")
    public UserResponse update(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        log.info("Update user {} by id {}", userId, userRequest);
        User user = mapper.toUser(userRequest);
        User userUpdate = service.update(userId, user);
        return mapper.toUserResponse(userUpdate);
    }
}

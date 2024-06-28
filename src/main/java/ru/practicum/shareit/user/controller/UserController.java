package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.controller.dto.UserRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService service;

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest userRequest) {
        log.info("Creating user {}", userRequest);
        return service.create(userRequest);
    }

    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable Long userId) {
        log.info("Get user by id {}", userId);
        return service.get(userId);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        log.info("Get all users ");
        return service.getAll();
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable Long userId) {
        log.info("Remove user by id {}", userId);
        service.remove(userId);
    }

    @PatchMapping("{userId}")
    public UserResponse update(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        log.info("Update user {} by id {}", userId, userRequest);
        return service.update(userId, userRequest);
    }
}

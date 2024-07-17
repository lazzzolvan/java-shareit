package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRequest userRequest) {
        log.info("Creating user {}", userRequest);
        return userClient.create(userRequest);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        log.info("Get user by id {}", userId);
        return userClient.get(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get all users ");
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable Long userId) {
        log.info("Remove user by id {}", userId);
        userClient.remove(userId);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @RequestBody UserRequest userRequest) {
        log.info("Update user {} by id {}", userId, userRequest);
        return userClient.update(userId, userRequest);
    }
}

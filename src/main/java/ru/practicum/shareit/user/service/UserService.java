package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(Long userId, User user);

    Boolean remove(Long userId);

    List<User> getAll();

    User get(Long id);
}

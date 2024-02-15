package ru.practicum.shareit.user.storage.memory;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(Long userId, User user);

    Boolean remove(Long userId);

    List<User> getAll();

    User get(Long id);
}

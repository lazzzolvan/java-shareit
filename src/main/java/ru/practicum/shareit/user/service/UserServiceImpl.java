package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.memory.UserStorage;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public User create(User user) {
        return storage.create(user);
    }

    @Override
    public User update(Long userId, User user) {
        return storage.update(userId, user);
    }

    @Override
    public Boolean remove(Long userId) {
        return storage.remove(userId);
    }

    @Override
    public List<User> getAll() {
        return storage.getAll();
    }

    @Override
    public User get(Long id) {
        return storage.get(id);
    }
}

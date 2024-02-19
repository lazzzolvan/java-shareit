package ru.practicum.shareit.user.storage.memory;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.DataNotCorrectException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> userStorage = new HashMap<>();

    private Long generateID = 0L;

    @Override
    public User create(User user) {
        for (User value : userStorage.values()) {
            if (value.getEmail().equals(user.getEmail()))
                throw new DataNotCorrectException("Пользователь с этой почтой уже занят");
        }
        user.setId(++generateID);
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User update(Long userId, User user) {
        if (!userStorage.containsKey(userId)) {
            throw new DataNotFoundException(String.format("User %s not found", userId));
        }
        if (user.getName() != null) {
            userStorage.get(userId).setName(user.getName());
        }
        if (user.getEmail() != null) {
            for (User value : userStorage.values()) {
                if (value.getEmail().equals(user.getEmail()) && !value.getId().equals(userId))
                    throw new DataNotCorrectException("Пользователь с этой почтой уже занят");
            }
            userStorage.get(userId).setEmail(user.getEmail());
        }
        return userStorage.get(userId);
    }

    @Override
    public Boolean remove(Long userId) {
        if (!userStorage.containsKey(userId)) {
            throw new DataNotFoundException(String.format("User %s id not found", userId));
        }
        userStorage.remove(userId);
        return true;
    }

    @Override
    public User get(Long id) {
        if (!userStorage.containsKey(id)) {
            throw new DataNotFoundException(String.format("User %s not found", id));
        }
        return userStorage.get(id);
    }

}

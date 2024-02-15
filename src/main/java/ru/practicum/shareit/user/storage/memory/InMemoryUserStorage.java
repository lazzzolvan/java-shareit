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
        User userModified = User.builder()
                .id(++generateID)
                .name(user.getName())
                .email(user.getEmail())
                .build();
        userStorage.put(userModified.getId(), userModified);
        return userModified;
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
        User userUpdate = User.builder()
                .id(userId)
                .build();
        if (user.getName() != null) {
            userUpdate.setName(user.getName());
        } else {
            userUpdate.setName(userStorage.get(userId).getName());
        }
        if (user.getEmail() != null) {
            for (User value : userStorage.values()) {
                if (value.getEmail().equals(user.getEmail()) && !value.getId().equals(userId))
                    throw new DataNotCorrectException("Пользователь с этой почтой уже занят");
            }
            userUpdate.setEmail(user.getEmail());

        } else {
            userUpdate.setEmail(userStorage.get(userId).getEmail());
        }
        userStorage.put(userId, userUpdate);
        return userUpdate;
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

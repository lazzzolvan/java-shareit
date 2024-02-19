package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.controller.dto.UserRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.memory.UserStorage;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage storage;
    private final UserMapper mapper;

    @Override
    public UserResponse create(UserRequest user) {
        User userModified = mapper.toUser(user);
        return mapper.toUserResponse(storage.create(userModified));
    }

    @Override
    public UserResponse update(Long userId, UserRequest user) {
        User userModified = mapper.toUser(user);
        return mapper.toUserResponse(storage.update(userId, userModified));
    }

    @Override
    public Boolean remove(Long userId) {
        return storage.remove(userId);
    }

    @Override
    public List<UserResponse> getAll() {
        return mapper.toUserResponseList(storage.getAll());
    }

    @Override
    public UserResponse get(Long id) {
        return mapper.toUserResponse(storage.get(id));
    }
}

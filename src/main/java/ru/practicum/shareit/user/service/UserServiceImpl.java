package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.user.controller.dto.UserRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserResponse create(UserRequest user) {
        User userModified = mapper.toUser(user);
        return mapper.toUserResponse(repository.save(userModified));
    }

    @Override
    @Transactional
    public UserResponse update(Long userId, UserRequest user) {
        User userModified = repository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id not found: " + userId));

        if (user.getName() != null) {
            userModified.setName(user.getName());
        }

        if (user.getEmail() != null) {
            userModified.setEmail(user.getEmail());
        }
        return mapper.toUserResponse(repository.save(userModified));
    }

    @Override
    @Transactional
    public void remove(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id not found: " + userId));
        repository.deleteById(userId);
    }

    @Override
    public List<UserResponse> getAll() {
        return mapper.toUserResponseList(repository.findAll());
    }

    @Override
    public UserResponse get(Long id) {
        return mapper.toUserResponse(repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User with id not found: " + id)));
    }
}

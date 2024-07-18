package ru.practicum.shareit.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserRequest {

    private final Long id;

    private final String name;

    private final String email;
}

package ru.practicum.shareit.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class UserRequest {

    private final Long id;

    @NotNull
    private final String name;

    @Email
    @NotNull
    private final String email;
}

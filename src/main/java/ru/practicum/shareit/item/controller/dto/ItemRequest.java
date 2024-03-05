package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
public class ItemRequest {

    private final Long id;

    @NotNull
    @NotBlank
    private final String name;

    @NotNull
    @Size(max = 200, min = 1)
    private final String description;

    @NotNull
    private final Boolean available;

    private final Long owner;

    private final String request;
}

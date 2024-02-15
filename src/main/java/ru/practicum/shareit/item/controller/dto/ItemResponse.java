package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ItemResponse {

    private final Long id;

    private final String name;

    private final String description;

    private final Boolean available;
}

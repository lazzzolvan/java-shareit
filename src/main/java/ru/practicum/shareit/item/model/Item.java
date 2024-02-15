package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Item {

    private final Long id;

    private  String name;

    private  String description;

    private  Boolean available;

    private final Long owner;

    private final String request;
}

package ru.practicum.shareit.user.model;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class User {

    private final Long id;

    private String name;

    private String email;
}

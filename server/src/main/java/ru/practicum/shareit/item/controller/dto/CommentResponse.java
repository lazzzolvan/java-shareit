package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;
}

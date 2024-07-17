package ru.practicum.shareit.item.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter

@AllArgsConstructor
@Builder
public class CommentRequest {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private Long authorId;
    private Long itemId;
    private LocalDateTime created;
}

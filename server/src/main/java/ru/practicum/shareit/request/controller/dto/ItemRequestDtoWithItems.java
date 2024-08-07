package ru.practicum.shareit.request.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.item.controller.dto.ItemResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDtoWithItems {

    private Long id;

    private String description;

    @JsonProperty("requestor")
    private Long userId;

    @JsonProperty("created")
    private LocalDateTime creationDate;

    private List<ItemResponse> items;
}

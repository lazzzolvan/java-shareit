package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemResponse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDtoWithItems {

    private Long id;

    @NotBlank
    private String description;

    @NotNull
    @JsonProperty("requestor")
    private Long userId;

    @JsonProperty("created")
    private LocalDateTime creationDate;

    private List<ItemResponse> items;
}

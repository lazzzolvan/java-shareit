package ru.practicum.shareit.request.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    private String description;


    @JsonProperty("requestor")
    private Long userId;


    @JsonProperty("created")
    private LocalDateTime creationDate;
}

package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.controller.dto.ItemRequestDto;
import ru.practicum.shareit.request.controller.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService requestService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(header) long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Request for request for item {} from user {} creation", itemRequestDto.getDescription(), userId);
        return requestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getItemRequestById(@RequestHeader(header) long userId,
                                                      @PathVariable long requestId) {
        log.info("Request for get item's request {} from user {}", requestId, userId);
        return requestService.getItemRequestById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getItemRequestsByOwnerId(@RequestHeader(header) long userId) {
        log.info("Request for get  user's {} requests for items", userId);
        return requestService.getItemRequestsByRequestorId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getItemRequests(@RequestHeader(header) long userId,
                                                         @RequestParam(name = "from") Integer from,
                                                         @RequestParam(name = "size") Integer size) {
        log.info("Request for get {} requests for items from {} request", size, from);
        return requestService.getItemRequests(userId, from, size);
    }
}

package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.controller.dto.ItemRequestDto;
import ru.practicum.shareit.request.controller.dto.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDtoWithItems getItemRequestById(Long userId, Long itemRequestId);

    List<ItemRequestDtoWithItems> getItemRequestsByRequestorId(Long userId);

    List<ItemRequestDtoWithItems> getItemRequests(Long userId, Integer from, Integer size);
}

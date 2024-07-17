package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.dto.ItemRequestDto;
import ru.practicum.shareit.request.controller.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRequestMapper mapper;

    private final Sort sortCreatedByDesc = Sort.by(Sort.Direction.DESC, "creationDate");

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long requesterId, ItemRequestDto itemRequestDto) {
        userService.get(requesterId);
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(User.builder()
                .id(requesterId)
                .build());
        itemRequest.setCreationDate(LocalDateTime.now());
        return mapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDtoWithItems getItemRequestById(Long requesterId, Long itemRequestId) {
        userService.get(requesterId);
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new DataNotFoundException("ItemRequest not found with id " + itemRequestId));
        List<Item> items = itemRepository.findByRequestRequesterId(itemRequest.getRequester().getId());
        return mapper.toItemRequestDtoWithItems(itemRequest, items);
    }

    @Override
    public List<ItemRequestDtoWithItems> getItemRequestsByRequestorId(Long requesterId) {
        userService.get(requesterId);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(requesterId, sortCreatedByDesc);
        List<ItemRequestDtoWithItems> itemRequestDtoWithItemsList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findByRequestRequesterId(itemRequest.getRequester().getId());
            ItemRequestDtoWithItems itemRequestDtoWithItems = mapper.toItemRequestDtoWithItems(itemRequest, items);
            itemRequestDtoWithItemsList.add(itemRequestDtoWithItems);
        }
        return itemRequestDtoWithItemsList;
    }

    @Override
    public List<ItemRequestDtoWithItems> getItemRequests(Long requesterId, Integer from, Integer size) {
            int pageNumber = from / size;
            Pageable page = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id"));
            return itemRequestRepository.findByRequesterIdNot(requesterId, page)
                    .stream()
                    .map(itemRequest -> mapper.toItemRequestDtoWithItems(itemRequest,
                            itemRepository.findByRequestRequesterId(itemRequest.getRequester().getId())))
                    .collect(Collectors.toList());
    }
}

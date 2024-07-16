package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotCorrectRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.dto.ItemRequestDto;
import ru.practicum.shareit.request.controller.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestMapper mapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateItemRequest() {
        Long requesterId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "Test Item Request", requesterId, LocalDateTime.now());
        UserResponse requesterResponse = UserResponse.builder().id(requesterId).build();
        User requester = User.builder()
                .id(requesterId)
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test Item Request")
                .requester(requester)
                .creationDate(LocalDateTime.now())
                .build();
        ItemRequestDto expectedDto = new ItemRequestDto(1L, "Test Item Request", requesterId, LocalDateTime.now());

        when(userService.get(requesterId)).thenReturn(requesterResponse);

        when(mapper.toItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(mapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(expectedDto);

        ItemRequestDto resultDto = itemRequestService.createItemRequest(requesterId, itemRequestDto);

        assertNotNull(resultDto);
        assertEquals(expectedDto.getId(), resultDto.getId());
        assertEquals(expectedDto.getDescription(), resultDto.getDescription());
        assertEquals(expectedDto.getUserId(), resultDto.getUserId());
        assertEquals(expectedDto.getCreationDate(), resultDto.getCreationDate());

        verify(userService, times(1)).get(requesterId);
        verify(mapper, times(1)).toItemRequest(itemRequestDto);
        verify(itemRequestRepository, times(1)).save(itemRequest);
        verify(mapper, times(1)).toItemRequestDto(itemRequest);
    }

    @Test
    void testGetItemRequestById() {
        Long requesterId = 1L;
        Long itemRequestId = 1L;
        UserResponse requesterResponse = UserResponse.builder().id(requesterId).build();
        User requester = User.builder()
                .id(requesterId)
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description("Test Item Request")
                .requester(requester)
                .creationDate(LocalDateTime.now())
                .build();
        List<Item> items = new ArrayList<>();
        ItemRequestDtoWithItems expectedDtoWithItems = new ItemRequestDtoWithItems(itemRequestId, "Test Item Request", requesterId, itemRequest.getCreationDate(), new ArrayList<>());

        when(userService.get(requesterId)).thenReturn(requesterResponse);

        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));

        when(itemRepository.findByRequestRequesterId(requesterId)).thenReturn(items);

        when(mapper.toItemRequestDtoWithItems(itemRequest, items)).thenReturn(expectedDtoWithItems);

        ItemRequestDtoWithItems resultDtoWithItems = itemRequestService.getItemRequestById(requesterId, itemRequestId);

        assertNotNull(resultDtoWithItems);
        assertEquals(expectedDtoWithItems.getId(), resultDtoWithItems.getId());
        assertEquals(expectedDtoWithItems.getDescription(), resultDtoWithItems.getDescription());
        assertEquals(expectedDtoWithItems.getUserId(), resultDtoWithItems.getUserId());
        assertEquals(expectedDtoWithItems.getCreationDate(), resultDtoWithItems.getCreationDate());

        verify(userService, times(1)).get(requesterId);
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
        verify(itemRepository, times(1)).findByRequestRequesterId(requesterId);
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest, items);
    }

    @Test
    void testGetItemRequestsByRequestorId() {
        Long requesterId = 1L;
        UserResponse requesterResponse = UserResponse.builder().id(requesterId).build();
        User requester = User.builder()
                .id(requesterId)
                .build();
        Long requesterId2 = 2L;
        UserResponse requesterResponse2 = UserResponse.builder().id(requesterId2).build();
        User requester2 = User.builder()
                .id(requesterId2)
                .build();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Item Request 1")
                .requester(requester)
                .creationDate(LocalDateTime.now().minusDays(1))
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Test Item Request 2")
                .requester(requester2)
                .creationDate(LocalDateTime.now())
                .build();
        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);
        List<Item> items1 = new ArrayList<>();
        List<Item> items2 = new ArrayList<>();
        ItemRequestDtoWithItems itemRequestDtoWithItems1 = new ItemRequestDtoWithItems(1L, "Test Item Request 1", requesterId, itemRequest1.getCreationDate(), new ArrayList<>());
        ItemRequestDtoWithItems itemRequestDtoWithItems2 = new ItemRequestDtoWithItems(2L, "Test Item Request 2", requesterId2, itemRequest2.getCreationDate(), new ArrayList<>());
        List<ItemRequestDtoWithItems> expectedDtoWithItemsList = List.of(itemRequestDtoWithItems1, itemRequestDtoWithItems2);

        when(userService.get(requesterId)).thenReturn(requesterResponse);

        when(itemRequestRepository.findByRequesterId(requesterId, Sort.by(Sort.Direction.DESC, "creationDate"))).thenReturn(itemRequests);

        when(itemRepository.findByRequestRequesterId(requesterId)).thenReturn(items1, items2);

        when(mapper.toItemRequestDtoWithItems(itemRequest1, items1)).thenReturn(itemRequestDtoWithItems1);
        when(mapper.toItemRequestDtoWithItems(itemRequest2, items2)).thenReturn(itemRequestDtoWithItems2);

        List<ItemRequestDtoWithItems> resultDtoWithItemsList = itemRequestService.getItemRequestsByRequestorId(requesterId);

        assertNotNull(resultDtoWithItemsList);
        assertEquals(expectedDtoWithItemsList.size(), resultDtoWithItemsList.size());
        assertEquals(expectedDtoWithItemsList.get(0).getId(), resultDtoWithItemsList.get(0).getId());
        assertEquals(expectedDtoWithItemsList.get(1).getId(), resultDtoWithItemsList.get(1).getId());

        verify(userService, times(1)).get(requesterId);
        verify(itemRequestRepository, times(1)).findByRequesterId(requesterId, Sort.by(Sort.Direction.DESC, "creationDate"));
        verify(itemRepository, times(1)).findByRequestRequesterId(requesterId); // Twice, once for each itemRequest
        verify(itemRepository, times(1)).findByRequestRequesterId(requesterId2); // Twice, once for each itemRequest
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest1, items1);
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest2, items2);
    }

    @Test
    void testGetItemRequests_NullFromAndSize() {
        Long requesterId = 1L;
        UserResponse requesterResponse = UserResponse.builder().id(requesterId).build();
        User requester = User.builder()
                .id(requesterId)
                .build();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Item Request 1")
                .requester(requester)
                .creationDate(LocalDateTime.now().minusDays(1))
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Test Item Request 2")
                .requester(requester)
                .creationDate(LocalDateTime.now())
                .build();
        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);
        List<Item> items1 = new ArrayList<>();
        List<Item> items2 = new ArrayList<>();
        ItemRequestDtoWithItems itemRequestDtoWithItems1 = new ItemRequestDtoWithItems(1L, "Test Item Request 1", requesterId, itemRequest1.getCreationDate(), new ArrayList<>());
        ItemRequestDtoWithItems itemRequestDtoWithItems2 = new ItemRequestDtoWithItems(2L, "Test Item Request 2", requesterId, itemRequest2.getCreationDate(), new ArrayList<>());
        List<ItemRequestDtoWithItems> expectedDtoWithItemsList = List.of(itemRequestDtoWithItems1, itemRequestDtoWithItems2);

        when(userService.get(requesterId)).thenReturn(requesterResponse);

        when(itemRequestRepository.findByRequesterIdNot(requesterId, Sort.by(Sort.Direction.DESC, "creationDate")))
                .thenReturn(itemRequests);

        when(itemRepository.findByRequestRequesterId(requesterId)).thenReturn(items1, items2);

        when(mapper.toItemRequestDtoWithItems(itemRequest1, items1)).thenReturn(itemRequestDtoWithItems1);
        when(mapper.toItemRequestDtoWithItems(itemRequest2, items2)).thenReturn(itemRequestDtoWithItems2);

        List<ItemRequestDtoWithItems> resultDtoWithItemsList = itemRequestService.getItemRequests(requesterId, null, null);

        assertNotNull(resultDtoWithItemsList);
        assertEquals(expectedDtoWithItemsList.size(), resultDtoWithItemsList.size());
        assertEquals(expectedDtoWithItemsList.get(0).getId(), resultDtoWithItemsList.get(0).getId());
        assertEquals(expectedDtoWithItemsList.get(1).getId(), resultDtoWithItemsList.get(1).getId());

        verify(userService, times(1)).get(requesterId);
        verify(itemRequestRepository, times(1)).findByRequesterIdNot(requesterId, Sort.by(Sort.Direction.DESC, "creationDate"));
        verify(itemRepository, times(2)).findByRequestRequesterId(requesterId); // Twice, once for each itemRequest
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest1, items1);
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest2, items2);
    }

    @Test
    void testGetItemRequests_InvalidPageParametersFromAndSize() {
        Long requesterId = 1L;

        assertThrows(NotCorrectRequestException.class, () -> itemRequestService.getItemRequests(requesterId, -1, 0));

        verify(userService, times(1)).get(requesterId);

        verifyNoInteractions(itemRequestRepository);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetItemRequests_InvalidPageParametersFrom() {
        Long requesterId = 1L;

        assertThrows(NotCorrectRequestException.class, () -> itemRequestService.getItemRequests(requesterId, -1, 1));

        verify(userService, times(1)).get(requesterId);

        verifyNoInteractions(itemRequestRepository);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetItemRequests_InvalidPageParametersSize() {
        Long requesterId = 1L;

        assertThrows(NotCorrectRequestException.class, () -> itemRequestService.getItemRequests(requesterId, 11, -1));

        verify(userService, times(1)).get(requesterId);

        verifyNoInteractions(itemRequestRepository);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetItemRequests_NullFrom() {
        Long requesterId = 1L;
        UserResponse requesterResponse = UserResponse.builder().id(requesterId).build();
        User requester = User.builder()
                .id(requesterId)
                .build();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Item Request 1")
                .requester(requester)
                .creationDate(LocalDateTime.now().minusDays(1))
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Test Item Request 2")
                .requester(requester)
                .creationDate(LocalDateTime.now())
                .build();
        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);
        List<Item> items1 = new ArrayList<>();
        List<Item> items2 = new ArrayList<>();
        ItemRequestDtoWithItems itemRequestDtoWithItems1 = new ItemRequestDtoWithItems(1L, "Test Item Request 1", requesterId, itemRequest1.getCreationDate(), new ArrayList<>());
        ItemRequestDtoWithItems itemRequestDtoWithItems2 = new ItemRequestDtoWithItems(2L, "Test Item Request 2", requesterId, itemRequest2.getCreationDate(), new ArrayList<>());
        List<ItemRequestDtoWithItems> expectedDtoWithItemsList = List.of(itemRequestDtoWithItems1, itemRequestDtoWithItems2);

        when(userService.get(requesterId)).thenReturn(requesterResponse);

        when(itemRequestRepository.findByRequesterIdNot(requesterId, Sort.by(Sort.Direction.DESC, "creationDate")))
                .thenReturn(itemRequests);

        when(itemRepository.findByRequestRequesterId(requesterId)).thenReturn(items1, items2);

        when(mapper.toItemRequestDtoWithItems(itemRequest1, items1)).thenReturn(itemRequestDtoWithItems1);
        when(mapper.toItemRequestDtoWithItems(itemRequest2, items2)).thenReturn(itemRequestDtoWithItems2);

        List<ItemRequestDtoWithItems> resultDtoWithItemsList = itemRequestService.getItemRequests(requesterId, null, 1);

        assertNotNull(resultDtoWithItemsList);
        assertEquals(expectedDtoWithItemsList.size(), resultDtoWithItemsList.size());
        assertEquals(expectedDtoWithItemsList.get(0).getId(), resultDtoWithItemsList.get(0).getId());
        assertEquals(expectedDtoWithItemsList.get(1).getId(), resultDtoWithItemsList.get(1).getId());

        verify(userService, times(1)).get(requesterId);
        verify(itemRequestRepository, times(1)).findByRequesterIdNot(requesterId, Sort.by(Sort.Direction.DESC, "creationDate"));
        verify(itemRepository, times(2)).findByRequestRequesterId(requesterId); // Twice, once for each itemRequest
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest1, items1);
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest2, items2);
    }

    @Test
    void testGetItemRequests_NullSize() {
        Long requesterId = 1L;
        UserResponse requesterResponse = UserResponse.builder().id(requesterId).build();
        User requester = User.builder()
                .id(requesterId)
                .build();
        ItemRequest itemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test Item Request 1")
                .requester(requester)
                .creationDate(LocalDateTime.now().minusDays(1))
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Test Item Request 2")
                .requester(requester)
                .creationDate(LocalDateTime.now())
                .build();
        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);
        List<Item> items1 = new ArrayList<>();
        List<Item> items2 = new ArrayList<>();
        ItemRequestDtoWithItems itemRequestDtoWithItems1 = new ItemRequestDtoWithItems(1L, "Test Item Request 1", requesterId, itemRequest1.getCreationDate(), new ArrayList<>());
        ItemRequestDtoWithItems itemRequestDtoWithItems2 = new ItemRequestDtoWithItems(2L, "Test Item Request 2", requesterId, itemRequest2.getCreationDate(), new ArrayList<>());
        List<ItemRequestDtoWithItems> expectedDtoWithItemsList = List.of(itemRequestDtoWithItems1, itemRequestDtoWithItems2);

        when(userService.get(requesterId)).thenReturn(requesterResponse);

        when(itemRequestRepository.findByRequesterIdNot(requesterId, Sort.by(Sort.Direction.DESC, "creationDate")))
                .thenReturn(itemRequests);

        when(itemRepository.findByRequestRequesterId(requesterId)).thenReturn(items1, items2);

        when(mapper.toItemRequestDtoWithItems(itemRequest1, items1)).thenReturn(itemRequestDtoWithItems1);
        when(mapper.toItemRequestDtoWithItems(itemRequest2, items2)).thenReturn(itemRequestDtoWithItems2);

        List<ItemRequestDtoWithItems> resultDtoWithItemsList = itemRequestService.getItemRequests(requesterId, 2, null);

        assertNotNull(resultDtoWithItemsList);
        assertEquals(expectedDtoWithItemsList.size(), resultDtoWithItemsList.size());
        assertEquals(expectedDtoWithItemsList.get(0).getId(), resultDtoWithItemsList.get(0).getId());
        assertEquals(expectedDtoWithItemsList.get(1).getId(), resultDtoWithItemsList.get(1).getId());

        verify(userService, times(1)).get(requesterId);
        verify(itemRequestRepository, times(1)).findByRequesterIdNot(requesterId, Sort.by(Sort.Direction.DESC, "creationDate"));
        verify(itemRepository, times(2)).findByRequestRequesterId(requesterId); // Twice, once for each itemRequest
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest1, items1);
        verify(mapper, times(1)).toItemRequestDtoWithItems(itemRequest2, items2);
    }
}

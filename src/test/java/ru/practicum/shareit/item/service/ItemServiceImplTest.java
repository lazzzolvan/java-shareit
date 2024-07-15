package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.NotCorrectRequestException;
import ru.practicum.shareit.item.controller.dto.CommentRequest;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.controller.dto.ItemRequest;
import ru.practicum.shareit.item.controller.dto.ItemResponse;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testCreateItem() {
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest(null, "Test Item", "Test description", true, userId, null, null, null, null);

        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Item item = Item.builder()
                .id(1L)
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.getAvailable())
                .owner(user)
                .build();
        when(itemMapper.toItem(itemRequest, user)).thenReturn(item);
        ItemResponse itemResponse = new ItemResponse(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null, null, null, null);
        when(itemMapper.toItemResponse(item)).thenReturn(itemResponse);

        when(itemRepository.save(item)).thenReturn(item);

        ItemResponse response = itemService.create(itemRequest, userId);

        assertNotNull(response);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testCreateItemWithRequest() {
        Long userId = 1L;
        ItemRequest itemRequest = new ItemRequest(null, "Test Item", "Test description", true, userId, 1L, null, null, null);

        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ru.practicum.shareit.request.model.ItemRequest itemRequest1 = ru.practicum.shareit.request.model.ItemRequest.builder()
                .id(1L)
                .requester(user)
                .build();
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));

        Item item = Item.builder()
                .id(1L)
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.getAvailable())
                .owner(user)
                .request(itemRequest1)
                .build();
        when(itemMapper.toItem(itemRequest, user)).thenReturn(item);
        ItemResponse itemResponse = new ItemResponse(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), itemRequest1.getId(), null, null, null);
        when(itemMapper.toItemResponse(item)).thenReturn(itemResponse);

        when(itemRepository.save(item)).thenReturn(item);

        ItemResponse response = itemService.create(itemRequest, userId);

        assertNotNull(response);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getRequest().getId(), response.getRequestId());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testUpdateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemRequest itemRequest = new ItemRequest(itemId, "Updated Item", "Updated description", false, userId, null, null, null, null);

        User owner = User.builder().id(userId).build();
        Item existingItem = Item.builder()
                .id(itemId)
                .name("Old Item")
                .description("Old description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        ItemResponse itemResponse = new ItemResponse(itemId, itemRequest.getName(), itemRequest.getDescription(), itemRequest.getAvailable(), userId, null, null, null);

        Item updatedItem = Item.builder()
                .id(itemId)
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.getAvailable())
                .owner(owner)
                .build();
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);
        when(itemMapper.toItemResponse(any(Item.class))).thenReturn(itemResponse);

        ItemResponse response = itemService.update(userId, itemId, itemRequest);

        assertNotNull(response);
        assertEquals(itemId, response.getId());
        assertEquals(itemRequest.getName(), response.getName());
        assertEquals(itemRequest.getDescription(), response.getDescription());
        assertFalse(response.getAvailable());
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(any(Item.class));
    }


    @Test
    void testRemoveItemSuccess() {
        Long itemId = 1L;
        Item item = Item.builder()
                .id(itemId)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        doNothing().when(itemRepository).deleteById(itemId);

        Boolean result = itemService.remove(itemId);

        assertTrue(result);
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void testRemoveItemNotFound() {
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Вызов метода и проверка исключения
        assertThrows(DataNotFoundException.class, () -> itemService.remove(itemId));
        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).deleteById(itemId);
    }

    @Test
    void testGetItemWithoutPage() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("Name")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .owner(user)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.now())
                .author(User.builder().id(userId).build())
                .item(item)
                .build();
        CommentResponse commentResponse = CommentResponse.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.now())
                .authorName(user.getName())
                .itemId(itemId)
                .build();
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

        List<CommentResponse> commentResponses = List.of(commentResponse);
        when(commentMapper.toCommentResponse(comment)).thenReturn(commentResponse);

        ItemResponse itemResponse = new ItemResponse(itemId, item.getName(), item.getDescription(), item.getAvailable(), null, null, null, commentResponses);
        when(itemMapper.toItemResponse(item)).thenReturn(itemResponse);

        ItemResponse response = itemService.get(itemId, userId, null, null);

        assertNotNull(response);
        assertEquals(itemId, response.getId());
        assertEquals("Test Item", response.getName());
        assertEquals("Test description", response.getDescription());
        assertEquals(1, response.getComments().size());
        assertEquals("Test comment", response.getComments().get(0).getText());
        verify(itemRepository, times(1)).findById(itemId);
        verify(commentRepository, times(1)).findAllByItemId(itemId);
    }

    @Test
    void testGetItemWithPage() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("name")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .owner(User.builder().id(userId).build())
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemResponse itemResponse = new ItemResponse(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), null, null, null, Collections.emptyList());
        when(itemMapper.toItemResponse(item)).thenReturn(itemResponse);

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.now())
                .author(User.builder().id(userId).build())
                .item(item)
                .build();
        when(commentRepository.findAllByItemId(item.getId(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start")))).thenReturn(new PageImpl<>(List.of(comment)));
        when(commentMapper.toCommentResponse(comment)).thenReturn(CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .itemId(item.getId())
                .build());

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .build();
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        when(bookingRepository.findAllByItemIdAndStartBefore(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(bookingPage);
        when(bookingMapper.toBookingShortDtoFromBooking(booking)).thenReturn(BookingShortDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .build());
        Page<Booking> bookings = new PageImpl<>(Collections.emptyList());
        when(bookingRepository.findAllByItemIdAndStartAfter(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(bookings);

        ItemResponse response = itemService.get(item.getId(), item.getOwner().getId(), 0, 10);

        assertNotNull(response);
        assertEquals(itemResponse.getId(), response.getId());
        assertEquals(itemResponse.getName(), response.getName());
        assertEquals(itemResponse.getDescription(), response.getDescription());
        assertEquals(1, response.getComments().size());
        assertEquals("Test comment", response.getComments().get(0).getText());
        assertNotNull(response.getLastBooking());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(commentRepository, times(1)).findAllByItemId(item.getId(), page);
        verify(bookingRepository, times(1)).findAllByItemIdAndStartBefore(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class));
        verify(bookingRepository, times(1)).findAllByItemIdAndStartAfter(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class));
    }

    @Test
    void testGetItemWithInvalidPage() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("name")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("Test Item")
                .description("Test description")
                .available(true)
                .owner(user)
                .build();

        assertThrows(NotCorrectRequestException.class, () -> {
            itemService.get(item.getId(), item.getOwner().getId(), -1, 0);
        });
    }


    @Test
    void testAddCommentToItem() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentRequest commentRequest = new CommentRequest(null, "Test comment", null, userId, itemId, null);

        User user = User.builder().id(userId).name("name").build();
        Item item = Item.builder().id(itemId).owner(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();
        CommentResponse commentResponse = CommentResponse.builder()
                .id(comment.getId())
                .itemId(comment.getItem().getId())
                .authorName(item.getOwner().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
        when(commentMapper.toComment(commentRequest)).thenReturn(comment);
        when(commentMapper.toCommentResponse(comment)).thenReturn(commentResponse);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment); // Используем any(Comment.class)

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .build();
        List<Booking> bookings = List.of(booking);
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(bookings);

        CommentResponse createdComment = itemService.createComment(userId, itemId, commentRequest);

        assertNotNull(createdComment);
        assertEquals("Test comment", createdComment.getText());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(commentMapper, times(1)).toComment(commentRequest); // Проверяем вызов маппера
        verify(commentRepository, times(1)).save(any(Comment.class)); // Проверяем вызов save с любым Comment
    }

    @Test
    void testAddCommentToItemBookingEmpty() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentRequest commentRequest = new CommentRequest(null, "Test comment", null, userId, itemId, null);

        User user = User.builder().id(userId).name("name").build();
        Item item = Item.builder().id(itemId).owner(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        assertThrows(NotCorrectRequestException.class, () -> itemService.createComment(userId, itemId, commentRequest));
    }


    @Test
    void testAddCommentToItemNotFound() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentRequest commentRequest = new CommentRequest(null, "Test comment", null, userId, itemId, null);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> itemService.createComment(userId, itemId, commentRequest));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).findById(itemId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testSearchItemWithPage() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();

        ItemResponse itemResponse1 = new ItemResponse(item1.getId(), item1.getName(), item1.getDescription(), item1.getAvailable(), null, null, null, Collections.emptyList());
        ItemResponse itemResponse2 = new ItemResponse(item2.getId(), item2.getName(), item2.getDescription(), item2.getAvailable(), null, null, null, Collections.emptyList());
        String searchName = "Description";
        int from = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(from / size, size);
        List<Item> items = List.of(item1, item2);
        when(itemRepository.findAll(pageable)).thenReturn(new PageImpl<>(items));
        when(itemMapper.toItemResponseOfList(anyList())).thenReturn(List.of(itemResponse1, itemResponse2));

        List<ItemResponse> responses = itemService.searchItem(searchName, from, size);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(itemRepository, times(1)).findAll(pageable);
        verify(itemMapper, times(1)).toItemResponseOfList(items);
    }

    @Test
    void testSearchItemWithEmptyName() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();

        String searchName = "";
        List<Item> emptyItems = new ArrayList<>();
        when(itemMapper.toItemResponseOfList(emptyItems)).thenReturn(Collections.emptyList());

        List<ItemResponse> responses = itemService.searchItem(searchName, null, null);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(itemMapper, times(1)).toItemResponseOfList(emptyItems);
    }

    @Test
    void testSearchItemWithInvalidPageParameters() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();

        assertThrows(NotCorrectRequestException.class, () -> itemService.searchItem("Description", -1, 2));
        assertThrows(NotCorrectRequestException.class, () -> itemService.searchItem("Description", 0, 0));

        verify(itemRepository, never()).findAll(any(Pageable.class));
        verify(itemMapper, never()).toItemResponseOfList(anyList());
    }

    @Test
    void testGetAllByUser() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("name")
                .build();
        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();

        ItemResponse itemResponse1 = new ItemResponse(item1.getId(), item1.getName(), item1.getDescription(), item1.getAvailable(), null, null, null, Collections.emptyList());
        ItemResponse itemResponse2 = new ItemResponse(item2.getId(), item2.getName(), item2.getDescription(), item2.getAvailable(), null, null, null, Collections.emptyList());

        Booking lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .item(item1)
                .booker(user)
                .build();

        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item1)
                .booker(user)
                .build();

        BookingShortDto lastBookingDto = BookingShortDto.builder()
                .id(lastBooking.getId())
                .start(lastBooking.getStart())
                .end(lastBooking.getEnd())
                .itemId(lastBooking.getItem().getId())
                .bookerId(lastBooking.getBooker().getId())
                .build();

        BookingShortDto nextBookingDto = BookingShortDto.builder()
                .id(nextBooking.getId())
                .start(nextBooking.getStart())
                .end(nextBooking.getEnd())
                .itemId(nextBooking.getItem().getId())
                .bookerId(nextBooking.getBooker().getId())
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .created(LocalDateTime.now())
                .item(item1)
                .author(User.builder().id(1L).name("User 1").build())
                .build();

        CommentResponse commentResponse = CommentResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .build();

        List<Item> items = List.of(item1, item2);
        List<ItemResponse> itemResponses = List.of(itemResponse1, itemResponse2);

        when(itemRepository.findAllByOwnerIdOrderByIdAsc(userId)).thenReturn(items);
        when(itemMapper.toItemResponse(item1)).thenReturn(itemResponse1);
        when(itemMapper.toItemResponse(item2)).thenReturn(itemResponse2);

        when(bookingRepository.findAllByItemIdInAndStartBefore(anyList(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking));

        when(bookingMapper.toBookingShortDtoFromBooking(lastBooking)).thenReturn(lastBookingDto);

        when(bookingRepository.findAllByItemIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(List.of(nextBooking));

        when(bookingMapper.toBookingShortDtoFromBooking(nextBooking)).thenReturn(nextBookingDto);

        when(commentRepository.findAllByItemId(item1.getId())).thenReturn(List.of(comment));
        when(commentMapper.toCommentResponse(comment)).thenReturn(commentResponse);

        List<ItemResponse> responses = itemService.getAllByUser(userId);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        ItemResponse response1 = responses.get(0);
        assertEquals(item1.getId(), response1.getId());
        assertEquals(lastBookingDto, response1.getLastBooking());
        assertEquals(nextBookingDto, response1.getNextBooking());
        assertEquals(1, response1.getComments().size());
        assertEquals(commentResponse, response1.getComments().get(0));

        ItemResponse response2 = responses.get(1);
        assertEquals(item2.getId(), response2.getId());
        assertNull(response2.getLastBooking());
        assertNull(response2.getNextBooking());
        assertEquals(0, response2.getComments().size());

        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(userId);
        verify(bookingRepository, times(1)).findAllByItemIdInAndStartBefore(anyList(), any(LocalDateTime.class), any(Sort.class));
        verify(bookingRepository, times(1)).findAllByItemIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Sort.class));
        verify(commentRepository, times(2)).findAllByItemId(anyLong());
    }
}

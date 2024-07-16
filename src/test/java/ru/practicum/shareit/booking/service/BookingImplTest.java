package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.NotCorrectRequestException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private BookingImpl bookingService;

    private User user;
    private Item item;
    private BookingShortDto bookingDto;
    private Booking booking;
    private List<Booking> bookingList;
    private List<BookingResponse> bookingResponseList;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();

        bookingDto = BookingShortDto.builder()
                .id(1L)
                .itemId(item.getId())
                .bookerId(user.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        booking = Booking.builder()
                .id(2L)
                .booker(user)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingList = new ArrayList<>();
        bookingList.add(booking);

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .status(BookingStatus.WAITING)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        bookingResponseList = new ArrayList<>();
        bookingResponseList.add(bookingResponse);

    }

    @Test
    void testCreateBooking() {
        User currentUser = User.builder()
                .id(2L)
                .name("name2")
                .build();
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        Booking booking = Booking.builder()
                .id(bookingDto.getId())
                .booker(user)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(Item.builder()
                        .id(bookingDto.getItemId())
                        .build())
                .status(BookingStatus.WAITING)
                .build();

        when(bookingMapper.toBookFromShort(bookingDto)).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setId(1L); // Setting an ID as if saved in DB
            return savedBooking;
        });

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(bookingDto.getId())
                .booker(user)
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .item(Item.builder()
                        .id(bookingDto.getItemId())
                        .build())
                .status(BookingStatus.WAITING)
                .build();
        when(bookingMapper.toBookingResponse(any(Booking.class), any(User.class), any(Item.class))).thenReturn(bookingResponse);

        BookingResponse response = bookingService.create(bookingDto, currentUser.getId());

        assertNotNull(response);
        assertEquals(user.getId(), response.getBooker().getId());
        assertEquals(item.getId(), response.getItem().getId());
        assertEquals(BookingStatus.WAITING, response.getStatus());

        verify(userRepository, times(1)).findById(currentUser.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.create(bookingDto, user.getId()));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_ItemNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.create(bookingDto, user.getId()));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_ItemNotAvailable() {
        item.setAvailable(false);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotCorrectRequestException.class, () -> bookingService.create(bookingDto, user.getId()));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testCreateBookingOwnerIsUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(DataNotFoundException.class, () -> bookingService.create(bookingDto, user.getId()));
    }

    @Test
    void testCreateBookingStartNull() {
        BookingShortDto bookingDto2 = BookingShortDto.builder()
                .id(1L)
                .itemId(item.getId())
                .bookerId(user.getId())
                .start(null)
                .end(LocalDateTime.now().plusDays(2))
                .build();

        User currentUser = User.builder()
                .id(2L)
                .name("name2")
                .build();
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        Booking booking = Booking.builder()
                .id(bookingDto2.getId())
                .booker(user)
                .start(null)
                .end(bookingDto2.getEnd())
                .item(Item.builder()
                        .id(bookingDto2.getItemId())
                        .build())
                .status(BookingStatus.WAITING)
                .build();


        BookingResponse bookingResponse = BookingResponse.builder()
                .id(bookingDto2.getId())
                .booker(user)
                .end(bookingDto2.getEnd())
                .start(null)
                .item(Item.builder()
                        .id(bookingDto2.getItemId())
                        .build())
                .status(BookingStatus.WAITING)
                .build();
        assertThrows(NotCorrectRequestException.class, () -> bookingService.create(bookingDto2, currentUser.getId()));
    }

    @Test
    void testCreateBookingEndNull() {
        BookingShortDto bookingDto2 = BookingShortDto.builder()
                .id(1L)
                .itemId(item.getId())
                .bookerId(user.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(null)
                .build();

        User currentUser = User.builder()
                .id(2L)
                .name("name2")
                .build();
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        Booking booking = Booking.builder()
                .id(bookingDto2.getId())
                .booker(user)
                .start(bookingDto2.getStart())
                .end(null)
                .item(Item.builder()
                        .id(bookingDto2.getItemId())
                        .build())
                .status(BookingStatus.WAITING)
                .build();


        BookingResponse bookingResponse = BookingResponse.builder()
                .id(bookingDto2.getId())
                .booker(user)
                .end(null)
                .start(bookingDto2.getStart())
                .item(Item.builder()
                        .id(bookingDto2.getItemId())
                        .build())
                .status(BookingStatus.WAITING)
                .build();
        assertThrows(NotCorrectRequestException.class, () -> bookingService.create(bookingDto2, currentUser.getId()));
    }

    @Test
    void testCreateBookingEndIsBeforeStart() {
        BookingShortDto bookingDto2 = BookingShortDto.builder()
                .id(1L)
                .itemId(item.getId())
                .bookerId(user.getId())
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        User currentUser = User.builder()
                .id(2L)
                .name("name2")
                .build();
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));


        Booking booking = Booking.builder()
                .id(bookingDto2.getId())
                .booker(user)
                .start(bookingDto2.getStart())
                .end(bookingDto2.getEnd())
                .item(Item.builder()
                        .id(bookingDto2.getItemId())
                        .build())
                .status(BookingStatus.WAITING)
                .build();


        BookingResponse bookingResponse = BookingResponse.builder()
                .id(bookingDto2.getId())
                .booker(user)
                .end(bookingDto2.getEnd())
                .start(bookingDto2.getStart())
                .item(Item.builder()
                        .id(bookingDto2.getItemId())
                        .build())
                .status(BookingStatus.WAITING)
                .build();
        assertThrows(NotCorrectRequestException.class, () -> bookingService.create(bookingDto2, currentUser.getId()));
    }

    @Test
    void testCreateBookingEndIsStart() {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(2);
        BookingShortDto bookingDto2 = BookingShortDto.builder()
                .id(1L)
                .itemId(item.getId())
                .bookerId(user.getId())
                .start(dateTime)
                .end(dateTime)
                .build();

        User currentUser = User.builder()
                .id(2L)
                .name("name2")
                .build();
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(NotCorrectRequestException.class, () -> bookingService.create(bookingDto2, currentUser.getId()));
    }

    @Test
    void testUpdateBookingApproved() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .status(BookingStatus.APPROVED)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(bookingMapper.toBookingResponse(any(), any(), any()))
                .thenReturn(bookingResponse);

        BookingResponse response = bookingService.update(booking.getId(), user.getId(), true);

        assertNotNull(response);
        assertEquals(BookingStatus.APPROVED, response.getStatus());
    }

    @Test
    void testUpdateBookingRejected() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .status(BookingStatus.REJECTED)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(bookingMapper.toBookingResponse(any(), any(), any()))
                .thenReturn(bookingResponse);

        BookingResponse response = bookingService.update(booking.getId(), user.getId(), false);

        assertNotNull(response);
        assertEquals(BookingStatus.REJECTED, response.getStatus());
    }

    @Test
    void testUpdateBookingInvalidBookerId() {
        assertThrows(DataNotFoundException.class, () -> bookingService.update(99L, booking.getBooker().getId(), true));
    }

    @Test
    void testUpdateBookingInvalidUser() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(DataNotFoundException.class, () -> bookingService.update(booking.getId(), 999L, true));
    }

    @Test
    void testUpdateBookingAlreadyProcessed() {
        booking.setStatus(BookingStatus.APPROVED); // Set status to APPROVED
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotCorrectRequestException.class, () -> bookingService.update(booking.getId(), user.getId(), true));
    }

    @Test
    void testGetById_ValidUserIsBooker_ReturnsBookingResponse() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .status(BookingStatus.REJECTED)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(bookingMapper.toBookingResponse(any(), any(), any()))
                .thenReturn(bookingResponse);

        BookingResponse response = bookingService.getById(booking.getId(), user.getId());

        assertNotNull(response);
        assertEquals(BookingResponse.class, response.getClass());
    }

    @Test
    void testGetById_ValidUserIsItemOwner_ReturnsBookingResponse() {
        User differentUser = User.builder()
                .id(3L)
                .name("Different User")
                .email("different@example.com")
                .build();
        item.setOwner(differentUser);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .status(BookingStatus.REJECTED)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(bookingMapper.toBookingResponse(any(), any(), any()))
                .thenReturn(bookingResponse);
        BookingResponse response = bookingService.getById(booking.getId(), differentUser.getId());

        assertNotNull(response);
        assertEquals(BookingResponse.class, response.getClass());
    }

    @Test
    void testGetById_UserIsNeitherBookerNorItemOwner_ThrowsDataNotFoundException() {
        User differentUser = User.builder()
                .id(3L)
                .name("Different User")
                .email("different@example.com")
                .build();

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(DataNotFoundException.class, () -> bookingService.getById(booking.getId(), differentUser.getId()));
    }

    @Test
    void testGetAllByUser_NoPaging_ReturnsBookingResponseList() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingMapper.toBookingResponseOfList(bookingList)).thenReturn(bookingResponseList);
        when(bookingRepository.findAllByBooker(user, Sort.by(Sort.Direction.DESC, "start"))).thenReturn(bookingList);
        Integer from = null;
        Integer size = null;

        List<BookingResponse> response = bookingService.getAllByUser(user.getId(), BookingState.ALL, from, size);

        assertNotNull(response);
        assertEquals(bookingResponseList.size(), response.size());
        assertEquals(BookingResponse.class, response.get(0).getClass());

        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findAllByBooker(user, Sort.by(Sort.Direction.DESC, "start"));
        verify(bookingMapper, times(1)).toBookingResponseOfList(bookingList);
    }

    @Test
    void testGetAllByUser_WithPaging_ReturnsBookingResponseList() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingMapper.toBookingResponseOfList(bookingList)).thenReturn(bookingResponseList);
        Integer from = 0;
        Integer size = 10;

        Page<Booking> bookingPage = new PageImpl<>(bookingList);
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));

        when(bookingRepository.findAllByBooker(user, page)).thenReturn(bookingPage);

        List<BookingResponse> response = bookingService.getAllByUser(user.getId(), BookingState.ALL, from, size);

        assertNotNull(response);
        assertEquals(bookingResponseList.size(), response.size());
        assertEquals(BookingResponse.class, response.get(0).getClass());

        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findAllByBooker(user, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start")));
        verify(bookingMapper, times(1)).toBookingResponseOfList(bookingList);
    }

    @Test
    void testGetAllByUser_InvalidPageParameters_ThrowsNotCorrectRequestException() {
        Integer from = -1;
        Integer size = 0;

        assertThrows(NotCorrectRequestException.class, () -> {
            bookingService.getAllByUser(user.getId(), BookingState.ALL, from, size);
        });

        verify(userRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).findAllByBooker(any(User.class), any(Pageable.class));
        verify(bookingMapper, never()).toBookingResponseOfList(anyList());
    }

    @Test
    void testGetBookingByUser() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Mock bookingRepository behavior based on BookingState
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        when(bookingRepository.findAllByBooker(user, pageable)).thenReturn(new PageImpl<>(bookingList));

        // Mock mapper behavior
        when(bookingMapper.toBookingResponseOfList(any())).thenReturn(new ArrayList<>()); // Adjust behavior if needed

        // Test with BookingState.ALL
        List<BookingResponse> responses = bookingService.getBookingByUser(1L, BookingState.ALL, pageable);
        assertNotNull(responses);
        assertEquals(0, responses.size()); // Assuming mapper.toBookingResponseOfList returns an empty list for this mock

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findAllByBooker(user, pageable);
        verify(bookingMapper, times(1)).toBookingResponseOfList(bookingList);
    }

    @Test
    void testGetBookingByUserThrowsExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingByUser(1L, BookingState.ALL, PageRequest.of(0, 10)));

        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void testGetBookingByUserWithoutPage() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        when(bookingRepository.findAllByBooker(user, sortByStartDesc)).thenReturn(bookingList);

        when(bookingMapper.toBookingResponseOfList(any())).thenReturn(new ArrayList<>());

        List<BookingResponse> responses = bookingService.getBookingByUserWithoutPage(1L, BookingState.ALL);
        assertNotNull(responses);
        assertEquals(0, responses.size());

        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findAllByBooker(user, sortByStartDesc);
        verify(bookingMapper, times(1)).toBookingResponseOfList(bookingList);
    }

    @Test
    void testGetBookingByUserWithoutPageThrowsExceptionWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingByUserWithoutPage(1L, BookingState.ALL));

        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void testGetBookingByUserWithoutPageAll() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Mock bookingRepository behavior
        Sort sortByStartDesc = Sort.by(Sort.Order.desc("start"));
        when(bookingRepository.findAllByBooker(user, sortByStartDesc)).thenReturn(bookingList);

        // Mock mapper behavior
        when(bookingMapper.toBookingResponseOfList(any())).thenReturn(new ArrayList<>()); // Adjust behavior if needed

        // Test with BookingState.ALL
        List<BookingResponse> responses = bookingService.getBookingByUserWithoutPage(1L, BookingState.ALL);
        assertNotNull(responses);
        assertEquals(0, responses.size()); // Assuming mapper.toBookingResponseOfList returns an empty list for this mock

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findAllByBooker(user, sortByStartDesc);
        verify(bookingMapper, times(1)).toBookingResponseOfList(bookingList);
    }

    @Test
    void testGetBookingByUserWithoutPageWaiting() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Mock bookingRepository behavior
        Sort sortByStartDesc = Sort.by(Sort.Order.desc("start"));
        when(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.WAITING, sortByStartDesc)).thenReturn(bookingList);

        // Mock mapper behavior
        when(bookingMapper.toBookingResponseOfList(any())).thenReturn(new ArrayList<>()); // Adjust behavior if needed

        // Test with BookingState.WAITING
        List<BookingResponse> responses = bookingService.getBookingByUserWithoutPage(1L, BookingState.WAITING);
        assertNotNull(responses);
        assertEquals(0, responses.size()); // Assuming mapper.toBookingResponseOfList returns an empty list for this mock

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findAllByBookerAndStatusEquals(user, BookingStatus.WAITING, sortByStartDesc);
        verify(bookingMapper, times(1)).toBookingResponseOfList(bookingList);
    }

    @Test
    void testGetBookingByUserWithoutPageRejected() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Mock bookingRepository behavior
        Sort sortByStartDesc = Sort.by(Sort.Order.desc("start"));
        when(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.REJECTED, sortByStartDesc)).thenReturn(bookingList);

        // Mock mapper behavior
        when(bookingMapper.toBookingResponseOfList(any())).thenReturn(new ArrayList<>()); // Adjust behavior if needed

        // Test with BookingState.REJECTED
        List<BookingResponse> responses = bookingService.getBookingByUserWithoutPage(1L, BookingState.REJECTED);
        assertNotNull(responses);
        assertEquals(0, responses.size()); // Assuming mapper.toBookingResponseOfList returns an empty list for this mock

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findAllByBookerAndStatusEquals(user, BookingStatus.REJECTED, sortByStartDesc);
        verify(bookingMapper, times(1)).toBookingResponseOfList(bookingList);
    }

    @Test
    void testGetBookingByUserWithoutPageUnsupportedState() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Test with unsupported BookingState
        assertThrows(NotCorrectRequestException.class, () -> bookingService.getBookingByUserWithoutPage(1L, BookingState.UNSUPPORTED_STATUS));

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verifyNoInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void testGetBookingByUserWithoutPageExceptionWhenUserNotFound() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Test that DataNotFoundException is thrown
        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingByUserWithoutPage(1L, BookingState.ALL));

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void testGetBookingByOwnerWithoutPageAll() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Mock bookingRepository behavior
        Sort sortByStartDesc = Sort.by(Sort.Order.desc("start"));
        when(bookingRepository.findAllByItemOwner(user, sortByStartDesc)).thenReturn(bookingList);

        BookingResponse bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .status(BookingStatus.REJECTED)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(bookingMapper.toBookingResponse(any(), any(), any()))
                .thenReturn(bookingResponse);

        // Mock mapper behavior
        when(bookingMapper.toBookingResponse(any(), any(), any())).thenReturn(bookingResponse); // Adjust behavior if needed

        // Test with BookingState.ALL
        List<BookingResponse> responses = bookingService.getBookingByOwnerWithoutPage(1L, BookingState.ALL);
        assertNotNull(responses);
        assertEquals(1, responses.size()); // Assuming mapper.toBookingResponse returns a list with one element for this mock

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findAllByItemOwner(user, sortByStartDesc);
        verify(bookingMapper, times(1)).toBookingResponse(booking, booking.getBooker(), booking.getItem());
    }

    @Test
    void testGetBookingByOwnerWithoutPageWaiting() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Mock bookingRepository behavior
        Sort sortByStartDesc = Sort.by(Sort.Order.desc("start"));
        when(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.WAITING, sortByStartDesc)).thenReturn(bookingList);
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .status(BookingStatus.REJECTED)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(bookingMapper.toBookingResponse(any(), any(), any()))
                .thenReturn(bookingResponse);
        when(bookingMapper.toBookingResponse(any(), any(), any())).thenReturn(bookingResponse);

        // Test with BookingState.WAITING
        List<BookingResponse> responses = bookingService.getBookingByOwnerWithoutPage(1L, BookingState.WAITING);
        assertNotNull(responses);
        assertEquals(1, responses.size()); // Assuming mapper.toBookingResponse returns a list with one element for this mock

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStatusEquals(user, BookingStatus.WAITING, sortByStartDesc);
        verify(bookingMapper, times(1)).toBookingResponse(booking, booking.getBooker(), booking.getItem());
    }

    @Test
    void testGetBookingByOwnerWithoutPageRejected() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Mock bookingRepository behavior
        Sort sortByStartDesc = Sort.by(Sort.Order.desc("start"));
        when(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.REJECTED, sortByStartDesc)).thenReturn(bookingList);
        BookingResponse bookingResponse = BookingResponse.builder()
                .id(booking.getId())
                .status(BookingStatus.REJECTED)
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(bookingMapper.toBookingResponse(any(), any(), any()))
                .thenReturn(bookingResponse);
        when(bookingMapper.toBookingResponse(any(), any(), any())).thenReturn(bookingResponse);

        // Test with BookingState.REJECTED
        List<BookingResponse> responses = bookingService.getBookingByOwnerWithoutPage(1L, BookingState.REJECTED);
        assertNotNull(responses);
        assertEquals(1, responses.size()); // Assuming mapper.toBookingResponse returns a list with one element for this mock

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).findAllByItemOwnerAndStatusEquals(user, BookingStatus.REJECTED, sortByStartDesc);
        verify(bookingMapper, times(1)).toBookingResponse(booking, booking.getBooker(), booking.getItem());
    }

    @Test
    void testGetBookingByOwnerWithoutPageUnsupportedState() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Test with unsupported BookingState
        assertThrows(NotCorrectRequestException.class, () -> bookingService.getBookingByOwnerWithoutPage(1L, BookingState.UNSUPPORTED_STATUS));

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void testGetBookingByOwnerWithoutPageThrowsExceptionWhenUserNotFound() {
        // Mock userRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Test that DataNotFoundException is thrown
        assertThrows(DataNotFoundException.class, () -> bookingService.getBookingByOwnerWithoutPage(1L, BookingState.ALL));

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    void testGetFullBooking() {
        // Mock bookingRepository behavior
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Perform the method call
        Booking result = bookingService.getFullBooking(1L);

        // Assertions
        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStatus(), result.getStatus());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        assertEquals(booking.getBooker().getName(), result.getBooker().getName());
        assertEquals(booking.getBooker().getEmail(), result.getBooker().getEmail());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getItem().getName(), result.getItem().getName());
        assertEquals(booking.getItem().getDescription(), result.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), result.getItem().getAvailable());
        assertEquals(booking.getItem().getOwner().getId(), result.getItem().getOwner().getId());
        assertEquals(booking.getItem().getOwner().getName(), result.getItem().getOwner().getName());
        assertEquals(booking.getItem().getOwner().getEmail(), result.getItem().getOwner().getEmail());

        // Verify interactions
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetFullBookingNotFound() {
        // Mock bookingRepository behavior
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // Perform the method call and assert exception
        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> bookingService.getFullBooking(1L));
        assertEquals("Бронь не найдена id 1", exception.getMessage());

        // Verify interactions
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookingByOwnerUnknownState() {
        // Mock repository response (not used in this test)
        Pageable pageable = Pageable.unpaged();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        // Call the service method and assert exception
        NotCorrectRequestException exception = assertThrows(NotCorrectRequestException.class, () -> {
            bookingService.getBookingByOwner(user.getId(), BookingState.UNSUPPORTED_STATUS, pageable);
        });

        // Assertions
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}


package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.controller.dto.BookingResponse;
import ru.practicum.shareit.booking.controller.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.NotCorrectRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;

    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
    private final Sort sortByStartAsc = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingResponse create(BookingShortDto bookingDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден id " + userId));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .map(itemCurrent -> {
                    User owner = itemCurrent.getOwner();
                    Item item1 = Item.builder()
                            .id(itemCurrent.getId())
                            .name(itemCurrent.getName())
                            .available(itemCurrent.getAvailable())
                            .description(itemCurrent.getDescription())
                            .request(itemCurrent.getRequest())
                            .owner(User.builder()
                                    .id(owner.getId())
                                    .name(owner.getName())
                                    .email(owner.getEmail())
                                    .build())
                            .build();
                    return item1;
                })
                .orElseThrow(() -> new DataNotFoundException("Item не найден с id " + bookingDto.getItemId()));

        if (!item.getAvailable())
            throw new NotCorrectRequestException("Предмет не доступен для брони");
        if (item.getOwner().getId().equals(user.getId()))
            throw new DataNotFoundException("Пользователь не может бронировать свои вещи");
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null)
            throw new NotCorrectRequestException("Нет даты для брони");
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().equals(bookingDto.getEnd()))
            throw new NotCorrectRequestException("Не верно указана дата");
        Booking booking = mapper.toBookFromShort(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return mapper.toBookingResponse(bookingRepository.save(booking), booking.getBooker(), booking.getItem());
    }

    @Override
    @Transactional
    public BookingResponse update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = getFullBooking(bookingId);
        if (!userId.equals(booking.getItem().getOwner().getId()))
            throw new DataNotFoundException(("Не правильный id пользователя " + userId));
        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new NotCorrectRequestException("Бронирование уже существует");

        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return mapper.toBookingResponse(bookingRepository.save(booking), booking.getBooker(), booking.getItem());
    }

    @Override
    public BookingResponse getById(Long bookingId, Long userId) {
        Booking booking = getFullBooking(bookingId);

        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId()))
            throw new DataNotFoundException("Бронь могут смотреть только пользователь, который создал бронь" +
                    " или владелец вещи");

        return mapper.toBookingResponse(booking, booking.getBooker(), booking.getItem());
    }

    @Override
    @Transactional
    public List<BookingResponse> getAllByUser(Long userId, BookingState state, Integer from, Integer size) {
        if (from == null || size == null) {
            return getBookingByUserWithoutPage(userId, state);
        } else if (from < 0 || size <= 0) {
            throw new NotCorrectRequestException("Not correct page parameters");
        }
        int pageNumber = from / size;
        Pageable page = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "start"));
        return getBookingByUser(userId, state, page);
    }

    @Override
    public List<BookingResponse> getAllByOwner(Long userId, BookingState state, Integer from, Integer size) {
        if (from == null || size == null) {
            return getBookingByOwnerWithoutPage(userId, state);
        } else if (from < 0 || size <= 0) {
            throw new NotCorrectRequestException("Not correct page parameters");
        }
        int pageNumber = from / size;
        Pageable page = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "start"));
        return getBookingByOwner(userId, state, page);
    }

    public Booking getFullBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .map(booking1 -> {
                    Item item = booking1.getItem();
                    User owner = item.getOwner();
                    User booker = booking1.getBooker();
                    Booking bookingCurrent = Booking.builder()
                            .id(booking1.getId())
                            .status(booking1.getStatus())
                            .start(booking1.getStart())
                            .end(booking1.getEnd())
                            .booker(User.builder()
                                    .id(booker.getId())
                                    .name(booker.getName())
                                    .email(booker.getEmail())
                                    .build())
                            .item(Item.builder()
                                    .id(item.getId())
                                    .name(item.getName())
                                    .available(item.getAvailable())
                                    .description(item.getDescription())
                                    .request(item.getRequest())
                                    .owner(User.builder()
                                            .id(owner.getId())
                                            .name(owner.getName())
                                            .email(owner.getEmail())
                                            .build())
                                    .build())
                            .build();
                    return bookingCurrent;
                })
                .orElseThrow(() -> new DataNotFoundException("Бронь не найдена id " + bookingId));
        return booking;
    }

    public List<BookingResponse> getBookingByOwnerWithoutPage(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользваоетль не найден с id " + userId));
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByItemOwner(user, sortByStartDesc));
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), sortByStartAsc));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sortByStartDesc));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sortByStartDesc));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.WAITING, sortByStartDesc));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.REJECTED, sortByStartDesc));
                break;
            default:
                throw new NotCorrectRequestException("Unknown state: " + BookingState.UNSUPPORTED_STATUS);
        }

        return bookings.stream().map(booking -> mapper.toBookingResponse(booking, booking.getBooker(), booking.getItem())).collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingByOwner(Long userId, BookingState state, Pageable page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользваоетль не найден с id " + userId));
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findAllByItemOwner(user, page).getContent());
                break;
            case CURRENT:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), page).getContent());
                break;
            case PAST:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), page).getContent());
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), page).getContent());
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.WAITING, page).getContent());
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEquals(user, BookingStatus.REJECTED, page).getContent());
                break;
            default:
                throw new NotCorrectRequestException("Unknown state: " + BookingState.UNSUPPORTED_STATUS);
        }

        return bookings.stream().map(booking -> mapper.toBookingResponse(booking, booking.getBooker(), booking.getItem())).collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingByUserWithoutPage(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользваоетль не найден с id " + userId));
        List<Booking> bookings = new ArrayList<>();

        if (state.equals(BookingState.ALL)) {
            bookings.addAll(bookingRepository.findAllByBooker(user, sortByStartDesc));
        } else if (state.equals(BookingState.CURRENT)) {
            bookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(), LocalDateTime.now(), sortByStartDesc));
        } else if (state.equals(BookingState.PAST)) {
            bookings.addAll(bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), sortByStartDesc));
        } else if (state.equals(BookingState.FUTURE)) {
            bookings.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sortByStartDesc));
        } else if (state.equals(BookingState.WAITING)) {
            bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.WAITING, sortByStartDesc));
        } else if (state.equals(BookingState.REJECTED)) {
            bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.REJECTED, sortByStartDesc));
        } else
            throw new NotCorrectRequestException("Unknown state: " + BookingState.UNSUPPORTED_STATUS);

        return mapper.toBookingResponseOfList(bookings);
    }

    public List<BookingResponse> getBookingByUser(Long userId, BookingState state, Pageable page) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользваоетль не найден с id " + userId));
        List<Booking> bookings = new ArrayList<>();

        if (state.equals(BookingState.ALL)) {
            bookings.addAll(bookingRepository.findAllByBooker(user, page).getContent());
        } else if (state.equals(BookingState.CURRENT)) {
            bookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(), LocalDateTime.now(), page).getContent());
        } else if (state.equals(BookingState.PAST)) {
            bookings.addAll(bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), page).getContent());
        } else if (state.equals(BookingState.FUTURE)) {
            bookings.addAll(bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), page).getContent());
        } else if (state.equals(BookingState.WAITING)) {
            bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.WAITING, page).getContent());
        } else if (state.equals(BookingState.REJECTED)) {
            bookings.addAll(bookingRepository.findAllByBookerAndStatusEquals(user, BookingStatus.REJECTED, page).getContent());
        } else
            throw new NotCorrectRequestException("Unknown state: " + BookingState.UNSUPPORTED_STATUS);

        return mapper.toBookingResponseOfList(bookings);
    }
}
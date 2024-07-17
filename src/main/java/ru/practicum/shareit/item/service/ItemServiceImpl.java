package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
    private final Sort sortByStartAsc = Sort.by(Sort.Direction.ASC, "start");
    private final Sort sortByItemAndStartAsc = Sort.by(Sort.Direction.ASC, "itemId", "start");

    @Override
    @Transactional
    public ItemResponse create(ItemRequest item, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден с id " + userId));

        Item itemModified = itemMapper.toItem(item, user);
        if (item.getRequestId() != null) {
            Optional<ru.practicum.shareit.request.model.ItemRequest> itemRequest = itemRequestRepository.findById(item.getRequestId());
            if (itemRequest.isPresent()) {
                itemModified.setRequest(itemRequest.get());
            }
        } else
            itemModified.setRequest(null);
        return itemMapper.toItemResponse(itemRepository.save(itemModified));
    }

    @Override
    @Transactional
    public ItemResponse update(Long userId, Long itemId, ItemRequest item) {
        Item itemModified = itemRepository.findById(itemId)
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
                .orElseThrow(() -> new DataNotFoundException(String.format("Item with %s id not found", itemId)));

        if (!itemModified.getOwner().getId().equals(userId))
            throw new DataNotFoundException(String.format("User id %s not found for item id %s", userId, itemId));

        if (item.getName() != null)
            itemModified.setName(item.getName());
        if (item.getDescription() != null)
            itemModified.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            itemModified.setAvailable(item.getAvailable());
        return itemMapper.toItemResponse(itemRepository.save(itemModified));
    }

    @Override
    public Boolean remove(Long itemId) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Item with %s id not found", itemId)));
        itemRepository.deleteById(itemId);
        return true;
    }

    @Override
    @Transactional
    public ItemResponse get(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Вещь не найдена с id " + itemId));
        ItemResponse itemResponse = itemMapper.toItemResponse(item);
        itemResponse.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentResponse).collect(Collectors.toList()));

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> lastBookings = bookingRepository.findAllByItemIdAndStartBefore(itemId, LocalDateTime.now(), sortByStartDesc);
            if (!lastBookings.isEmpty()) {
                itemResponse.setLastBooking(bookingMapper.toBookingShortDtoFromBooking(lastBookings.get(0)));
            }

            if (itemResponse.getLastBooking() != null) {
                List<Booking> nextBookings = bookingRepository.findAllByItemIdAndStartAfter(itemId, LocalDateTime.now(), sortByStartAsc);
                if (!nextBookings.isEmpty()) {
                    itemResponse.setNextBooking(bookingMapper.toBookingShortDtoFromBooking(nextBookings.get(0)));
                }
            }
        }
        return itemResponse;
    }

    @Override
    public List<ItemResponse> getAllByUser(Long userId, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable page = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));

        List<Item> items = itemRepository.findAllByOwnerId(userId, page).getContent();
        List<ItemResponse> itemDtos = items.stream().map(itemMapper::toItemResponse).collect(Collectors.toList());
        List<Long> itemIds = itemDtos.stream().map(ItemResponse::getId).collect(Collectors.toList());

        List<Booking> lastBookings = bookingRepository
                .findAllByItemIdInAndStartBefore(itemIds, LocalDateTime.now(), sortByItemAndStartAsc);

        Map<Long, List<Booking>> itemToBooking = lastBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        itemDtos.forEach(itemDto -> {
            List<Booking> bookings = itemToBooking.get(itemDto.getId());
            if (bookings != null && !bookings.isEmpty()) {
                itemDto.setLastBooking(bookingMapper.toBookingShortDtoFromBooking(bookings.get(0)));

                List<Booking> nextBooking = bookingRepository
                        .findAllByItemIdAndStartAfter(itemDto.getId(), bookings.get(0).getStart(), sortByStartAsc);
                if (!nextBooking.isEmpty()) {
                    itemDto.setNextBooking(bookingMapper.toBookingShortDtoFromBooking(nextBooking.get(0)));
                }
            }
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream()
                    .map(commentMapper::toCommentResponse).collect(Collectors.toList()));
        });

        return itemDtos;
    }

    @Override
    public List<ItemResponse> searchItem(String name, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable page = PageRequest.of(pageNumber, size);
        return searchItemWithPage(name, page);
    }

    @Override
    public CommentResponse createComment(Long userId, Long itemId, CommentRequest commentDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с таким id не найден: " + commentDto.getAuthorId()));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Вещь с таким id не найдена: " + commentDto.getItemId()));

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(user.getId(), item.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new NotCorrectRequestException("У данного пользователя нет бронирований этой вещи");
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    public List<ItemResponse> searchItemWithPage(String name, Pageable page) {
        List<Item> items = new ArrayList<>();
        if (name.isEmpty())
            return itemMapper.toItemResponseOfList(items);
        items = itemRepository.search(name, page).getContent();
        return itemMapper.toItemResponseOfList(items);
    }
}

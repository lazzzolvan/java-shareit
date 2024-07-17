package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                          BookingStatus status, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBooker(User user, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus status, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwner(User owner, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, BookingStatus status, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemIdInAndStartBefore(List<Long> itemIds, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemIdAndStartAfter(Long id, LocalDateTime start, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemIdAndStartBefore(Long id, LocalDateTime start, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByBooker(User user, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByBookerAndStartBeforeAndEndAfter(User user, LocalDateTime start, LocalDateTime end, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus status, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByItemOwner(User owner, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime now, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime now, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByItemOwnerAndStatusEquals(User owner, BookingStatus status, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByItemIdAndStartBefore(Long id, LocalDateTime start, Pageable page);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Page<Booking> findAllByItemIdAndStartAfter(Long id, LocalDateTime start, Pageable page);
}

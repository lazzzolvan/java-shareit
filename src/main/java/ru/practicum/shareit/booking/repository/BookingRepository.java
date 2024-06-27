package ru.practicum.shareit.booking.repository;

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
    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBookerAndEndBefore(User user, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBookerAndStartAfter(User user, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByBookerAndStatusEquals(User user, BookingStatus status, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwner(User owner, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartAsc(User owner, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime now, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, BookingStatus status, Sort sort);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemIdInAndStartBeforeOrderByItemIdAscStartAsc(List<Long> itemIds, LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long id, LocalDateTime start);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemIdAndStartBeforeOrderByStartDesc(Long id, LocalDateTime start);
}

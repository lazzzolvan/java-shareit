package ru.practicum.shareit.booking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    public void setUp() {
        user1 = userRepository.save(new User(null, "User1", "user1@example.com"));
        user2 = userRepository.save(new User(null, "User2", "user2@example.com"));

        item1 = itemRepository.save(new Item(null, "Item1", "Description1", true, user1, null));
        item2 = itemRepository.save(new Item(null, "Item2", "Description2", true, user2, null));

        booking1 = bookingRepository.save(new Booking(null, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(2), item1, user1, BookingStatus.APPROVED));
        booking2 = bookingRepository.save(new Booking(null, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), item2, user2, BookingStatus.WAITING));
    }

    @Test
    void testFindAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(user1.getId(), item1.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByBooker() {
        List<Booking> bookings = bookingRepository.findAllByBooker(user1, Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByBookerAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user1, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3), Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByBookerAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndEndBefore(user1, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "end"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByBookerAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStartAfter(user2, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByBookerAndStatusEquals() {
        List<Booking> bookings = bookingRepository.findAllByBookerAndStatusEquals(user2, BookingStatus.WAITING, Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByItemOwner() {
        List<Booking> bookings = bookingRepository.findAllByItemOwner(user1, Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByItemOwnerAndStartBeforeAndEndAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(1), Sort.by(Sort.Direction.DESC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking2);
    }



    @Test
    void testFindAllByItemOwnerAndEndBefore() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndEndBefore(user1, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "end"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByItemOwnerAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStartAfter(user2, LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByItemOwnerAndStatusEquals() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStatusEquals(user2, BookingStatus.WAITING, Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByItemIdInAndStartBefore() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStartBefore(List.of(item1.getId(), item2.getId()), LocalDateTime.now().plusDays(1), Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByItemIdAndStartAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStartAfter(item2.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByItemIdAndStartBefore() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStartBefore(item1.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.ASC, "start"));

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByBookerWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByBooker(user1, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByBookerAndStartBeforeAndEndAfterWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user1, LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(3), pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByBookerAndEndBeforeWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "end"));
        Page<Booking> page = bookingRepository.findAllByBookerAndEndBefore(user1, LocalDateTime.now(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByBookerAndStartAfterWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByBookerAndStartAfter(user2, LocalDateTime.now(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByBookerAndStatusEqualsWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByBookerAndStatusEquals(user2, BookingStatus.WAITING, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByItemOwnerWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByItemOwner(user1, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByItemOwnerAndStartBeforeAndEndAfterWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
        Page<Booking> page = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user2, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(1), pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByItemOwnerAndEndBeforeWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "end"));
        Page<Booking> page = bookingRepository.findAllByItemOwnerAndEndBefore(user1, LocalDateTime.now(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByItemOwnerAndStartAfterWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByItemOwnerAndStartAfter(user2, LocalDateTime.now(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByItemOwnerAndStatusEqualsWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByItemOwnerAndStatusEquals(user2, BookingStatus.WAITING, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking2);
    }

    @Test
    void testFindAllByItemIdAndStartBeforeWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByItemIdAndStartBefore(item1.getId(), LocalDateTime.now(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking1);
    }

    @Test
    void testFindAllByItemIdAndStartAfterWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "start"));
        Page<Booking> page = bookingRepository.findAllByItemIdAndStartAfter(item2.getId(), LocalDateTime.now(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(booking2);
    }
}

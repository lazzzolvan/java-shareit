package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");
        user2 = userRepository.save(user2);

        request1 = new ItemRequest();
        request1.setDescription("Request 1");
        request1.setRequester(user1);
        request1.setCreationDate(LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("Request 2");
        request2.setRequester(user2);
        request2.setCreationDate(LocalDateTime.now().minusDays(2));
        itemRequestRepository.save(request2);

        request3 = new ItemRequest();
        request3.setDescription("Request 3");
        request3.setRequester(user1);
        request3.setCreationDate(LocalDateTime.now().minusDays(3));
        itemRequestRepository.save(request3);
    }

    @Test
    void testFindByRequesterId() {
        Sort sort = Sort.by(Sort.Direction.DESC, "creationDate");
        List<ItemRequest> requests = itemRequestRepository.findByRequesterId(user1.getId(), sort);

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0)).isEqualTo(request1);
        assertThat(requests.get(1)).isEqualTo(request3);
    }

    @Test
    void testFindByRequesterIdNot() {
        Sort sort = Sort.by(Sort.Direction.DESC, "creationDate");
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(user1.getId(), sort);

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0)).isEqualTo(request2);
    }

    @Test
    void testFindByRequesterIdNotWithPageable() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "creationDate"));
        Page<ItemRequest> requestsPage = itemRequestRepository.findByRequesterIdNot(user1.getId(), pageable);

        assertThat(requestsPage.getTotalElements()).isEqualTo(1);
        assertThat(requestsPage.getContent()).hasSize(1);
        assertThat(requestsPage.getContent().get(0)).isEqualTo(request2);
    }

    @Test
    void testFindByRequesterIdNotWithEmptyPage() {
        Pageable pageable = PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "creationDate"));
        Page<ItemRequest> requestsPage = itemRequestRepository.findByRequesterIdNot(user1.getId(), pageable);

        assertThat(requestsPage.getTotalElements()).isEqualTo(1);
        assertThat(requestsPage.getContent()).hasSize(0);
    }
}

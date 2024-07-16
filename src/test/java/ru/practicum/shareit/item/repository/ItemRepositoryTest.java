package ru.practicum.shareit.item.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;
    private ItemRequest itemRequest1;

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

        itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("Request 1");
        itemRequest1.setRequester(user1);
        itemRequest1 = itemRequestRepository.save(itemRequest1);

        item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setAvailable(false);
        item2.setOwner(user1);
        item2.setRequest(itemRequest1);
        item2 = itemRepository.save(item2);

        item3 = new Item();
        item3.setName("Another Item");
        item3.setDescription("Another Description");
        item3.setAvailable(true);
        item3.setOwner(user2);
        item3 = itemRepository.save(item3);
    }


    @Test
    void testFindAllByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId());

        assertThat(items).hasSize(2);
        assertThat(items.get(0)).isEqualTo(item1);
        assertThat(items.get(1)).isEqualTo(item2);
    }

    @Test
    void testSearch() {
        List<Item> items = itemRepository.search("Item");

        assertThat(items).hasSize(2);
        assertThat(items).contains(item1, item3);
    }

    @Test
    void testSearchNotAvailable() {
        List<Item> items = itemRepository.search("Description2");

        assertThat(items).isEmpty();
    }

    @Test
    void testFindByRequestRequesterId() {
        List<Item> items = itemRepository.findByRequestRequesterId(itemRequest1.getRequester().getId());

        assertThat(items).hasSize(1);
        assertThat(items.get(0)).isEqualTo(item2);
    }

    @Test
    void testFindByRequestRequesterIdNoItems() {
        List<Item> items = itemRepository.findByRequestRequesterId(999L);

        assertThat(items).isEmpty();
    }

    @Test
    void testFindAllWithPageable() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Item> itemsPage = itemRepository.findAll(pageable);

        assertThat(itemsPage.getTotalElements()).isEqualTo(3);
        assertThat(itemsPage.getContent()).hasSize(2);
    }

    @Test
    void testFindAllWithPageableSecondPage() {
        Pageable pageable = PageRequest.of(1, 2);
        Page<Item> itemsPage = itemRepository.findAll(pageable);

        assertThat(itemsPage.getTotalElements()).isEqualTo(3);
        assertThat(itemsPage.getContent()).hasSize(1);
        assertThat(itemsPage.getContent().get(0)).isEqualTo(item3);
    }

    @Test
    void testSearchItemsByNameOrDescriptionIgnoreCase() {
        // Given
        String searchTerm = "item";
        Pageable pageable = PageRequest.of(0, 10); // Page 0, size 10

        // When
        Page<Item> resultPage = itemRepository.search(searchTerm, pageable);
        List<Item> resultList = resultPage.getContent();

        // Then
        assertThat(resultList).hasSize(3); // Expecting 2 items found
        assertThat(resultList).extracting(Item::getName).contains("Item1", "Item2", "Another Item");
    }

}

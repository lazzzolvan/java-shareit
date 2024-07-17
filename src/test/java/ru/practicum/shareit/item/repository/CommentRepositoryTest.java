package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item1;
    private Item item2;
    private User user1;
    private User user2;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    public void setUp() {
        user1 = userRepository.save(new User(null, "User1", "user1@example.com"));
        user2 = userRepository.save(new User(null, "User2", "user2@example.com"));

        item1 = itemRepository.save(new Item(null, "Item1", "Description1", true, user1, null));
        item2 = itemRepository.save(new Item(null, "Item2", "Description2", true, user2, null));

        comment1 = commentRepository.save(new Comment(null, "Comment1", item1, user1, LocalDateTime.now()));
        comment2 = commentRepository.save(new Comment(null, "Comment2", item1, user2, LocalDateTime.now()));
    }

    @Test
    void testFindAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item1.getId());

        assertThat(comments).hasSize(2);
        assertThat(comments.get(0)).isEqualTo(comment1);
        assertThat(comments.get(1)).isEqualTo(comment2);
    }

    @Test
    void testFindAllByItemIdWithPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("created").descending());
        Page<Comment> page = commentRepository.findAllByItemId(item1.getId(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFindAllByItemIdWithPaging_NoComments() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "created"));
        Page<Comment> page = commentRepository.findAllByItemId(item2.getId(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getContent()).isEmpty();
    }
}

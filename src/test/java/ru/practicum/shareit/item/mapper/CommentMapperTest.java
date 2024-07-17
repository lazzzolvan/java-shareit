package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.controller.dto.CommentRequest;
import ru.practicum.shareit.item.controller.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @InjectMocks
    private CommentMapperImpl commentMapper;

    private User author;
    private Item item;
    private Comment comment;
    private CommentRequest commentRequest;
    private CommentResponse commentResponse;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(1L)
                .name("Test Author")
                .email("author@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Item description")
                .available(true)
                .owner(author)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Test Comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        commentRequest = new CommentRequest(
                1L,
                "Test Comment",
                "Test Author",
                author.getId(),
                item.getId(),
                LocalDateTime.now()
        );

        commentResponse = CommentResponse.builder()
                .id(1L)
                .text("Test Comment")
                .authorName("Test Author")
                .itemId(item.getId())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void testToComment() {
        Comment mappedComment = commentMapper.toComment(commentRequest);

        assertEquals(commentRequest.getId(), mappedComment.getId());
        assertEquals(commentRequest.getText(), mappedComment.getText());
        assertEquals(commentRequest.getCreated(), mappedComment.getCreated());
    }

    @Test
    void testToCommentResponse() {
        CommentResponse mappedCommentResponse = commentMapper.toCommentResponse(comment);

        assertEquals(comment.getId(), mappedCommentResponse.getId());
        assertEquals(comment.getText(), mappedCommentResponse.getText());
        assertEquals(comment.getAuthor().getName(), mappedCommentResponse.getAuthorName());
        assertEquals(comment.getItem().getId(), mappedCommentResponse.getItemId());
        assertEquals(comment.getCreated(), mappedCommentResponse.getCreated());
    }

    @Test
    void testToCommentResponseOfList() {
        List<Comment> comments = Collections.singletonList(comment);

        List<CommentResponse> mappedCommentResponses = commentMapper.toCommentResponseOfList(comments);

        assertEquals(comments.size(), mappedCommentResponses.size());

        CommentResponse mappedCommentResponse = mappedCommentResponses.get(0);
        assertEquals(comment.getId(), mappedCommentResponse.getId());
        assertEquals(comment.getText(), mappedCommentResponse.getText());
        assertEquals(comment.getAuthor().getName(), mappedCommentResponse.getAuthorName());
        assertEquals(comment.getItem().getId(), mappedCommentResponse.getItemId());
        assertEquals(comment.getCreated(), mappedCommentResponse.getCreated());
    }
}

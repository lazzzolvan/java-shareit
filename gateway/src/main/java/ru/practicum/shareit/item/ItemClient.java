package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.controller.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit.server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, ItemRequest itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemRequest itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> remove(Long userId, Long itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentRequest text) {
        return post("/" + itemId + "/comment", userId, text);
    }

    public ResponseEntity<Object> searchItem(String text, Integer from, Integer size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllByUser(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> get(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }
}
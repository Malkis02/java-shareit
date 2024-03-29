package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import javax.validation.Valid;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, @Valid ItemDto dto) {
        return post("",userId,dto);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId,userId);
    }

    public ResponseEntity<Object> getAllItems(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> search(long userId, Integer from, Integer size, String text) {
        Map<String, Object> parameters = Map.of(
                "text",text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}",userId,parameters);
    }

    public ResponseEntity<Object> updateItem(long userId, Long itemId, UpdateItemDto dto) {
        return patch("/" + itemId,userId, dto);
    }

    public ResponseEntity<Object> createComment(long userId, @Valid CommentDto dto, Long itemId) {
        return post("/" + itemId + "/comment",userId,dto);
    }
}

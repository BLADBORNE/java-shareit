package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comment.dto.CreationCommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.UpdateItem;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createNewItem(int userId, ItemCreationDto item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(int userId, UpdateItem item, int itemId) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> getItemById(int userId, int itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUsersItems(int userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemsForSearch(int userId, String text) {
        Map<String, Object> parameters = Map.of("text", text);

        return get("/search" + "?text={text}", (long) userId, parameters);
    }

    public ResponseEntity<Object> addCommentToItem(int userId, int itemId, CreationCommentDto dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}
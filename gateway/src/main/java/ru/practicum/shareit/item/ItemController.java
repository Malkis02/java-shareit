package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient client;


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto item) {
        return client.createItem(userId,item);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@Min(1L) @PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody UpdateItemDto item) {
        return client.updateItem(userId,itemId,item);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Min(1L) @PathVariable Long itemId) {
        return client.getItem(userId,itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(defaultValue = "0") int from,
                                                            @RequestParam(defaultValue = "10") int size) {
        return client.getAllItems(userId,from,size);
    }

    @GetMapping("search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return client.search(userId,from,size,text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody CommentDto comment,
                                                    @PathVariable Long itemId) {
        return client.createComment(userId,comment,itemId);
    }
}

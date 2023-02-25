package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    private final ItemRepositoryMapper mapper;

    private final CommentMapper commentMapper;


    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemDto item) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.mapToItemDto(itemService.create(item,userId)));
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody UpdateItemDto item) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.mapToItemDto(itemService.update(mapper.mapToItem(item, userId), itemId)));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemBookingDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toItemBookingDto(itemService.get(itemId, userId)));
    }

    @GetMapping
    public ResponseEntity<List<ItemBookingDto>> getAllItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(defaultValue = "0") int from,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getAll(userId,from,size)
                        .stream()
                        .map(mapper::toItemBookingDto)
                        .sorted(Comparator.comparingLong(ItemBookingDto::getId))
                        .collect(Collectors.toList()));
    }

    @GetMapping("search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.search(text,from,size)
                        .stream()
                        .map(mapper::mapToItemDto)
                        .collect(Collectors.toList()));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody CommentDto comment,
                                                    @PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(commentMapper.toCommentDto(
                        itemService.createComment(commentMapper.toComment(comment), itemId, userId)));
    }
}

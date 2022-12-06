package ru.practicum.shareit.item.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    private final ItemMapper itemMapper;

    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }
    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Valid @RequestBody ItemDto item){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemMapper.mapToItemDto(itemService.create(itemMapper.mapToItem(item,userId))));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@Min(1L) @PathVariable Long itemId,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @Valid @RequestBody UpdateItemDto item){
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemMapper.mapToItemDto(itemService.update(itemMapper.mapToItem(item,userId),itemId)));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDto> getItem(@Min(1L) @PathVariable Long itemId){
        return ResponseEntity.status(HttpStatus.OK).body(itemMapper.mapToItemDto(itemService.get(itemId)));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItem(@RequestHeader("X-Sharer-User-Id") Long userId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getAll(userId)
                        .stream()
                        .map(itemMapper::mapToItemDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text){
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.search(text)
                        .stream()
                        .map(itemMapper::mapToItemDto)
                        .collect(Collectors.toList()));
    }
}

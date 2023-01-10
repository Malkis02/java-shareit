package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestMapper mapper;

    private final ItemRequestService itemRequestService;


    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody ItemRequestDto item) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.toItemRequestDto(itemRequestService.create(mapper.toItemRequestEntity(item,userId),userId)));
    }


    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long requestId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.toItemRequestDto(itemRequestService.get(userId,requestId)));
    }
}

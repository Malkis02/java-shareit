package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
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

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Min(0)@RequestParam(defaultValue = "0") int from,
            @Min(1)@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getAll(userId,from,size)
                        .stream()
                        .map(mapper::toItemRequestDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping()
    public ResponseEntity<List<ItemRequestDto>> getAllWithoutPage(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemRequestService.getAll(userId)
                        .stream()
                        .map(mapper::toItemRequestDto)
                        .collect(Collectors.toList()));
    }
}

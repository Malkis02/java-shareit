package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateResponseDto;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private final BookingRepositoryMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody BookingCreateRequestDto booking) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingMapper.toBookingDto(bookingService
                        .create(bookingMapper.toBookingEntity(booking), userId, booking.getItemId())));
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Min(1L) @PathVariable Long bookingId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingMapper.toBookingDto(bookingService.get(bookingId, userId)));
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingUpdateResponseDto> update(@RequestParam("approved") Boolean approved,
                                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @Min(1L) @PathVariable Long bookingId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingMapper.toBookingUpdateResponseDto(bookingService.approve(bookingId, userId, approved)));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) BookingState state,
            @Min(0)@RequestParam(defaultValue = "0") int from,
            @Min(1)@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getAll(userId, state,from,size)
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("owner")
    public ResponseEntity<List<BookingDto>> getOwnerItemsAll(
            @RequestParam(defaultValue = "ALL", required = false) BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Min(0)@RequestParam(defaultValue = "0") int from,
            @Min(1)@RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookingService.getAllOwnerItems(userId, state,from,size).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList()));
    }

}

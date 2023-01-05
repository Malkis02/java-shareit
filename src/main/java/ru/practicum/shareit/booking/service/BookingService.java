package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingEntity create(BookingEntity booking, Long userId, Long itemId);

    BookingEntity get(Long bookingId, Long userId);

    List<BookingEntity> getAll(Long userId, BookingState state);

    List<BookingEntity> getAllOwnerItems(Long userId, BookingState state);

    BookingEntity approve(Long bookingId, Long userId, Boolean approved);
}

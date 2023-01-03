package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.UnsupportedStateException;

import java.util.List;

public interface BookingService {
    Booking create(Booking booking, Long userId, Long itemId);

    Booking get(Long bookingId, Long userId);

    List<Booking> getAll(Long userId, BookingState state) throws UnsupportedStateException;

    List<Booking> getAllOwnerItems(Long userId, BookingState state) throws UnsupportedStateException;

    Booking approve(Long bookingId, Long userId, Boolean approved);
}

package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;

    private final UserService userService;

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingEntity create(BookingEntity booking, Long userId, Long itemId) {
        UserEntity user = userService.get(userId);
        ItemEntity item = itemService.getItem(itemId);
        validation(booking);
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException();
        }
        if (Objects.equals(userId, item.getOwner().getId())) {
            throw new NotFoundException("Нельзя брать в аренду у самого себя");
        }
        if (Objects.nonNull(item.getLastBooking()) || Objects.nonNull(item.getNextBooking())) {
            List<BookingEntity> bookings = repository.findAllByItem(item);
            for (BookingEntity b : bookings) {
                if (!booking.getStart().isBefore(b.getEnd()) || !booking.getEnd().isAfter(b.getStart())) {
                    continue;
                }
                if (booking.getStart().isAfter(b.getStart()) && booking.getEnd().isBefore(b.getEnd())) {
                    throw new NotFoundException("Вещь занята на это время");
                }
            }
        }
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return repository.save(booking);
    }

    @Override
    public BookingEntity get(Long bookingId, Long userId) {
        BookingEntity booking = repository.findById(bookingId).orElseThrow(NotFoundException::new);
        if (Objects.equals(userId, booking.getItem().getOwner().getId()) || Objects.equals(userId, booking.getBooker().getId())) {
            return booking;
        }
        throw new NotFoundException("Аренда не найдена");
    }

    @Override
    public List<BookingEntity> getAll(Long userId, BookingState state, int from, int size) {
        List<BookingEntity> result;
        UserEntity booker = userService.get(userId);
        switch (state) {
            case ALL:
                result = repository.findAllByBookerOrderByStartDesc(booker, PageRequest.of((from / size), size));
                break;
            case CURRENT:
                result = repository.findCurrentByBookerOrderByStartDesc(booker, LocalDateTime.now(), PageRequest.of((from / size), size));
                break;
            case PAST:
                result = repository.findPastByBookerOrderByStartDesc(booker, LocalDateTime.now(), PageRequest.of((from / size), size));
                break;
            case FUTURE:
                result = repository.findFutureByBookerOrderByStartDesc(booker, LocalDateTime.now(), PageRequest.of((from / size), size));
                break;
            case WAITING:
                result = repository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING, PageRequest.of((from / size), size));
                break;
            case REJECTED:
                result = repository.findAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED, PageRequest.of((from / size), size));
                break;
            case UNSUPPORTED_STATUS:
            default:
                throw new UnsupportedStateException();
        }
        return result;
    }

    @Override
    public List<BookingEntity> getAllOwnerItems(Long userId, BookingState state, int from, int size) {
        List<BookingEntity> result;
        UserEntity owner = userService.get(userId);
        switch (state) {
            case ALL:
                result = repository.findAllByOwnerItemsOrderByStartDesc(owner, PageRequest.of((from / size), size));
                break;
            case CURRENT:
                result = repository.findCurrentByOwnerItemsOrderByStartDesc(owner, LocalDateTime.now(), PageRequest.of((from / size), size));
                break;
            case PAST:
                result = repository.findPastByOwnerItemsOrderByStartDesc(owner, LocalDateTime.now(), PageRequest.of((from / size), size));
                break;
            case FUTURE:
                result = repository.findFutureByOwnerItemsOrderByStartDesc(owner, LocalDateTime.now(), PageRequest.of((from / size), size));
                break;
            case WAITING:
                result = repository.findAllByOwnerItemsAndStatusOrderByStartDesc(owner, BookingStatus.WAITING, PageRequest.of((from / size), size));
                break;
            case REJECTED:
                result = repository.findAllByOwnerItemsAndStatusOrderByStartDesc(owner, BookingStatus.REJECTED, PageRequest.of((from / size), size));
                break;
            case UNSUPPORTED_STATUS:
            default:
                throw new UnsupportedStateException();
        }
        return result;
    }

    @Override
    @Transactional
    public BookingEntity approve(Long bookingId, Long userId, Boolean approved) {
        BookingEntity stored = repository.findById(bookingId).orElseThrow(NotFoundException::new);
        if (!Objects.equals(userId, stored.getItem().getOwner().getId())) {
            throw new NotFoundException();
        }
        if (!Objects.equals(BookingStatus.WAITING, stored.getStatus())) {
            throw new UnsupportedStatusException();
        }
        stored.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return repository.save(stored);

    }

    private void validation(BookingEntity booking) {
        LocalDateTime end = booking.getEnd();
        LocalDateTime start = booking.getStart();
        if (end.isBefore(LocalDateTime.now()) || end.isBefore(start) || start.isBefore(LocalDateTime.now())) {
            throw new IllegalTimeException("Некорректное время аренды");
        }

    }


}

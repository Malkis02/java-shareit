package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;

    private final UserService userService;

    private final ItemService itemService;

    private final BookingRepositoryMapper mapper;

    private final UserRepositoryMapper userMapper;

    private final ItemRepositoryMapper itemRepositoryMapper;



    @Override
    public Booking create(Booking booking,Long userId,Long itemId) {
        User user = userService.get(userId);
        Item item = itemService.getItem(itemId);
        validation(booking);
        if (Objects.isNull(item) && Objects.isNull(user)) {
            throw new NotFoundException("Юзер или вещь не найдены");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException();
        }
        if (Objects.equals(userId,item.getOwner().getId())) {
            throw new NotFoundException("Нельзя брать в аренду у самого себя");
        }
        booking.setItem(itemRepositoryMapper.toItemEntity(item));
        booking.setBooker(userMapper.toEntity(user));
        booking.setStatus(BookingStatus.WAITING);
        return mapper.toBooking(repository.save(mapper.toEntity(booking)));
    }

    @Override
    public Booking get(Long bookingId,Long userId) {
        Booking booking = repository.findById(bookingId)
                .map(mapper::toBooking)
                .orElseThrow(NotFoundException::new);
        if (Objects.equals(userId,booking.getItem().getOwner().getId())
                || Objects.equals(userId,booking.getBooker().getId())) {
            return booking;
        }
        throw new NotFoundException("Аренда не найдена");
    }

    @Override
    public List<Booking> getAll(Long userId,BookingState state) throws UnsupportedStateException {
        List<BookingEntity> result;
        UserEntity booker = userMapper.toEntity(userService.get(userId));
        switch (state) {
            case ALL:
                result = repository.findAllByBookerOrderByStartDesc(booker);
                break;
            case CURRENT:
                result = repository.findCurrentByBooker(booker, Timestamp.valueOf(LocalDateTime.now()));
                break;
            case PAST:
                result = repository.findPastByBooker(booker, Timestamp.valueOf(LocalDateTime.now()));
                break;
            case FUTURE:
                result = repository.findFutureByBooker(booker, Timestamp.valueOf(LocalDateTime.now()));
                break;
            case WAITING:
                result = repository.findAllByBookerAndStatusOrderByStartDesc(booker,BookingStatus.WAITING);
                break;
            case REJECTED:
                result = repository.findAllByBookerAndStatusOrderByStartDesc(booker,BookingStatus.REJECTED);
                break;
            case UNSUPPORTED_STATUS:
            default:
                throw new UnsupportedStateException();
        }
        return result.stream()
                .map(mapper::toBooking)
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> getAllOwnerItems(Long userId, BookingState state) throws UnsupportedStateException {
        List<BookingEntity> result;
        UserEntity owner = userMapper.toEntity(userService.get(userId));
        switch (state) {
            case ALL:
                result = repository.findAllByOwnerItems(owner);
                break;
            case CURRENT:
                result = repository.findCurrentByOwnerItems(owner, Timestamp.valueOf(LocalDateTime.now()));
                break;
            case PAST:
                result = repository.findPastByOwnerItems(owner, Timestamp.valueOf(LocalDateTime.now()));
                break;
            case FUTURE:
                result = repository.findFutureByOwnerItems(owner, Timestamp.valueOf(LocalDateTime.now()));
                break;
            case WAITING:
                result = repository.findAllByOwnerItemsAndStatusOrderByStartDesc(owner,BookingStatus.WAITING);
                break;
            case REJECTED:
                result = repository.findAllByOwnerItemsAndStatusOrderByStartDesc(owner,BookingStatus.REJECTED);
                break;
            case UNSUPPORTED_STATUS:
            default:
                throw new UnsupportedStateException();
        }
        return result.stream()
                .map(mapper::toBooking)
                .collect(Collectors.toList());
    }

    @Override
    public Booking approve(Long bookingId, Long userId, Boolean approved) {
        BookingEntity stored = repository.findById(bookingId)
                .orElseThrow(NotFoundException::new);
        if (!Objects.equals(userId,stored.getItem().getOwner().getId())) {
            throw new NotFoundException();
        }
        if (!Objects.equals(BookingStatus.WAITING,stored.getStatus())) {
            throw new UnsupportedStatusException();
        }
        stored.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            return mapper.toBooking(repository.save(stored));

    }

    private void validation(Booking booking) {
        LocalDateTime end = booking.getEnd();
        LocalDateTime start = booking.getStart();
        if (end.isBefore(LocalDateTime.now()) || end.isBefore(start) || start.isBefore(LocalDateTime.now())) {
            throw new IllegalTimeException("Некорректное время аренды");
        }

    }


}

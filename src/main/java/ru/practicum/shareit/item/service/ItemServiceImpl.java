package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.entity.BookingEntity;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final BookingRepositoryMapper bookingRepositoryMapper;

    private final ItemRepositoryMapper mapper;

    private final CommentRepositoryMapper commentRepositoryMapper;

    private final CommentRepository commentRepository;

    @Override
    public ItemEntity create(ItemEntity item, Long userId) {
        UserEntity user = userService.get(userId);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public ItemEntity update(ItemEntity item, Long itemId) {
        validate(itemId, item);
        ItemEntity stored = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        mapper.updateEntity(item, stored);
        return itemRepository.save(stored);
    }

    @Override
    public ItemBookingDto get(Long itemId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        ItemEntity itemStored = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        ItemBookingDto itemBookingDto = mapper.toItemBookingDto(itemStored);
        if (itemStored.getOwner().getId().equals(userId)) {
            BookingShortDto lastBooking =
                    bookingRepository.findAllByItem(
                                    itemStored)
                            .stream()
                            .map(bookingRepositoryMapper::toLastBookingDto)
                            .filter(bookingShortDto -> bookingShortDto.getStart().isBefore(now))
                            .max(Comparator.comparing(BookingShortDto::getStart))
                            .orElse(null);
            BookingShortDto nextBooking = bookingRepository.findAllByItem(
                            itemStored)
                    .stream()
                    .map(bookingRepositoryMapper::toLastBookingDto)
                    .filter(bookingShortDto -> bookingShortDto.getStart().isAfter(now))
                    .max(Comparator.comparing(BookingShortDto::getStart))
                    .orElse(null);
            itemBookingDto.setLastBooking(lastBooking);
            itemBookingDto.setNextBooking(nextBooking);
        }
        return itemBookingDto;
    }

    @Override
    public ItemEntity getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<ItemBookingDto> getAll(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<ItemEntity> stored = itemRepository.findAllByOwnerId(userId);
        List<ItemBookingDto> itemList = new ArrayList<>();
        for (ItemEntity item : stored) {
            if (item.getOwner().getId().equals(userId)) {
                List<BookingEntity> bookingList = bookingRepository.findAllByItem(item);
                ItemBookingDto itemBookingDto = mapper.toItemBookingDto(item);
                itemBookingDto.setLastBooking(bookingList
                        .stream()
                        .map(bookingRepositoryMapper::toLastBookingDto)
                        .filter(bookingShortDto -> bookingShortDto.getStart().isBefore(now))
                        .max(Comparator.comparing(BookingShortDto::getStart))
                        .orElse(null));
                itemBookingDto.setNextBooking(bookingList
                        .stream()
                        .map(bookingRepositoryMapper::toLastBookingDto)
                        .filter(bookingShortDto -> bookingShortDto.getStart().isAfter(now))
                        .max(Comparator.comparing(BookingShortDto::getStart))
                        .orElse(null));
                itemList.add(itemBookingDto);
            }
        }
        return itemList;
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment, Long itemId, Long userId) {
        UserEntity user = userService.get(userId);
        if (comment.getText().isEmpty() || comment.getText().isBlank()) {
            throw new ItemNotAvailableException();
        }
        if (!bookingRepository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new ItemNotAvailableException();
        }
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        CommentEntity commentEntity = commentRepositoryMapper.toEntity(comment, user, itemId);
        return commentRepositoryMapper.toComment(commentRepository.save(commentEntity), user, itemId);
    }

    public List<ItemEntity> search(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text));
    }

    private void validate(Long itemId, ItemEntity item) {
        Long itemOwnerId = itemRepository.findById(itemId).orElseThrow().getOwner().getId();
        UserEntity user = userService.get(item.getOwner().getId());
        if (!itemOwnerId.equals(user.getId())) {
            throw new NotFoundException();
        }
    }
}

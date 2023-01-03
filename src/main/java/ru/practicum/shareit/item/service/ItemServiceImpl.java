package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final BookingRepositoryMapper bookingRepositoryMapper;

    private final ItemMapper itemMapper;

    private final ItemRepositoryMapper mapper;

    private final CommentRepositoryMapper commentRepositoryMapper;

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item create(Item item,Long userId) {
        UserEntity user = userService.get(userId);
            return mapper.toItem(itemRepository.save(mapper.toItemEntity(item)));
    }

    @Override
    @Transactional
    public Item update(Item item, Long itemId) {
        validate(itemId, item);
        ItemEntity stored = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        mapper.updateEntity(item,stored);
        return mapper.toItem(itemRepository.save(stored));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBookingDto get(Long itemId,Long userId) {
        LocalDateTime now = LocalDateTime.now();
        ItemEntity itemStored = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        Item item = mapper.toItem(itemStored);
        Set<CommentDto> comments = commentRepository.findAllByItem(itemStored)
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toSet());

        ItemBookingDto itemBookingDto = itemMapper.toItemBookingDto(item);
        if (item.getOwner().getId().equals(userId)) {
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
    @Transactional(readOnly = true)
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .map(mapper::toItem)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemBookingDto> getAll(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<ItemEntity> stored = itemRepository.findAllByOwnerId(userId);
        List<ItemBookingDto> itemList = new ArrayList<>();
        for (ItemEntity item:stored) {
            if (item.getOwner().getId().equals(userId)) {
                ItemBookingDto itemBookingDto = itemMapper.toItemBookingDto(item);
                itemBookingDto.setLastBooking(bookingRepository.findAllByItem(
                                item)
                        .stream()
                        .map(bookingRepositoryMapper::toLastBookingDto)
                        .filter(bookingShortDto -> bookingShortDto.getStart().isBefore(now))
                        .max(Comparator.comparing(BookingShortDto::getStart))
                        .orElse(null));
                itemBookingDto.setNextBooking(bookingRepository.findAllByItem(
                                item)
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

    @Transactional
    public Comment createComment(Comment comment,Long itemId,Long userId) {
        UserEntity user = userService.get(userId);
        if (comment.getText().isEmpty() || comment.getText().isBlank()) {
            throw new ItemNotAvailableException();
        }
        if (!bookingRepository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                itemId,userId, BookingStatus.APPROVED,Timestamp.valueOf(LocalDateTime.now()))) {
                throw new ItemNotAvailableException();
        }
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        CommentEntity commentEntity = commentRepositoryMapper.toEntity(comment,user,itemId);
        Comment stored = commentRepositoryMapper.toComment(commentRepository.save(commentEntity),user,itemId);
        return stored;
    }


    @Transactional(readOnly = true)
    public List<Item> search(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        return itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text,text)
                .stream()
                .map(mapper::toItem)
                .collect(Collectors.toList());
    }

    private void validate(Long itemId, Item item) {
        Long itemOwnerId = itemRepository.findById(itemId).orElseThrow().getOwner().getId();
        UserEntity user = userService.get(item.getOwner().getId());
        if (!itemOwnerId.equals(user.getId())) {
            throw new NotFoundException();
        }
    }
}

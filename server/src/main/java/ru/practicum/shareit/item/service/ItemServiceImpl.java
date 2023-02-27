package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserService userService;

    private final BookingRepository bookingRepository;

    private final ItemRepositoryMapper mapper;

    private final CommentRepositoryMapper commentRepositoryMapper;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository requestRepository;


    @Override
    @Transactional
    public ItemEntity create(ItemDto item, Long userId) {
        ItemEntity newItem = mapper.mapToItem(item,userId);
         if (item.getRequestId() != null) {
             ItemRequestEntity itemRequest = requestRepository.findById(item.getRequestId())
                     .orElseThrow(() -> new NotFoundException("Запроса с id=" +
                             item.getRequestId() + " нет"));
             newItem.setRequest(itemRequest);
         }
        newItem.setOwner(userService.get(userId));
        return itemRepository.save(newItem);
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
    public ItemEntity get(Long itemId, Long userId) {
        ItemEntity itemStored = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        if (itemStored.getOwner().getId().equals(userId)) {
            itemStored.setLastBooking(bookingRepository.findFirstByItemAndStatusIsOrderByStartAsc(
                            itemStored,BookingStatus.APPROVED)
                    .orElse(null));
            itemStored.setNextBooking(bookingRepository.findFirstByItemAndStatusIsOrderByEndDesc(
                            itemStored,BookingStatus.APPROVED)
                    .orElse(null));
        }
        return itemStored;
    }

    @Override
    public ItemEntity getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<ItemEntity> getAll(Long userId, int from, int size) {
        List<ItemEntity> stored = itemRepository.findAllByOwnerId(userId, PageRequest.of((from / size), size));
        List<ItemEntity> itemList = new ArrayList<>();
        for (ItemEntity item : stored) {
            if (item.getOwner().getId().equals(userId)) {
                item.setLastBooking(bookingRepository.findFirstByItemAndStatusIsOrderByStartAsc(
                        item,BookingStatus.APPROVED)
                        .orElse(null));
                item.setNextBooking(bookingRepository.findFirstByItemAndStatusIsOrderByEndDesc(
                        item,BookingStatus.APPROVED)
                        .orElse(null));
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment, Long itemId, Long userId) {
        UserEntity user = userService.get(userId);
        ItemEntity item = getItem(itemId);
        if (comment.getText().isEmpty() || comment.getText().isBlank()) {
            throw new ItemNotAvailableException();
        }
        if (!bookingRepository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new ItemNotAvailableException("Bookings not found");
        }
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        CommentEntity commentEntity = commentRepositoryMapper.toEntity(comment, user, item);
        return commentRepositoryMapper.toComment(commentRepository.save(commentEntity), user, item);
    }

    @Override
    public List<ItemEntity> search(String text, int from, int size) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(itemRepository
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                        text, text, PageRequest.of((from / size), size)));
    }

    private void validate(Long itemId, ItemEntity item) {
        Long itemOwnerId = itemRepository.findById(itemId).orElseThrow().getOwner().getId();
        UserEntity user = userService.get(item.getOwner().getId());
        if (!itemOwnerId.equals(user.getId())) {
            throw new NotFoundException();
        }
    }
}

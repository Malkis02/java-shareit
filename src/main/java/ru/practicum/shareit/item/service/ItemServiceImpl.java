package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
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

    private final UserRepositoryMapper userMapper;

    private final BookingRepository bookingRepository;

    private final BookingRepositoryMapper bookingRepositoryMapper;

    private final ItemMapper itemMapper;

    private final ItemRepositoryMapper mapper;

    private final CommentRepositoryMapper commentRepositoryMapper;

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    @Override
    public Item create(Item item,Long userId) {
        User user = userService.get(userId);
        if (Objects.nonNull(user)) {
            return mapper.toItem(itemRepository.save(mapper.toItemEntity(item)));
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public Item update(Item item, Long itemId) {
        validate(itemId, item);
        ItemEntity stored = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        mapper.updateEntity(item,stored);
        return mapper.toItem(itemRepository.save(stored));
    }

    @Override
    public ItemBookingDto get(Long itemId,Long userId) {
        ItemEntity itemStored = itemRepository.findById(itemId).orElseThrow(NotFoundException::new);
        Item item = mapper.toItem(itemStored);
        if (item.getOwner().getId().equals(userId)) {
            BookingShortDto lastBooking =
                    bookingRepository.findFirstByItemAndStartBeforeOrderByStartDesc(
                                    itemStored, Timestamp.valueOf(LocalDateTime.now()))
                            .map(bookingRepositoryMapper::toLastBookingDto)
                            .orElse(null);
            BookingShortDto nextBooking = bookingRepository.findFirstByItemAndStartAfterOrderByStart(
                            itemStored, Timestamp.valueOf(LocalDateTime.now()))
                    .map(bookingRepositoryMapper::toLastBookingDto)
                    .orElse(null);
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
        }
        item.setComments(new HashSet<>(commentRepository.findAllByItem(itemStored)));
        return itemMapper.toItemBookingDto(item);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .map(mapper::toItem)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<ItemBookingDto> getAll(Long userId) {
        List<ItemEntity> stored = itemRepository.findAllByOwnerId(userId);
        List<ItemBookingDto> itemList = new ArrayList<>();
        for (ItemEntity item:stored) {
            if (item.getOwner().getId().equals(userId)) {
                ItemBookingDto itemBookingDto = itemMapper.toItemBookingDto(item);
                itemBookingDto.setLastBooking(bookingRepository.findFirstByItemAndStartBeforeOrderByStartDesc(
                                item, Timestamp.valueOf(LocalDateTime.now()))
                        .map(bookingRepositoryMapper::toLastBookingDto)
                        .orElse(null));
                itemBookingDto.setNextBooking(bookingRepository.findFirstByItemAndStartAfterOrderByStart(
                                item, Timestamp.valueOf(LocalDateTime.now()))
                        .map(bookingRepositoryMapper::toLastBookingDto)
                        .orElse(null));
                itemList.add(itemBookingDto);
            }
        }
        return itemList;
    }

    public Comment createComment(Comment comment,Long itemId,Long userId) {
        User user = userService.get(userId);
        ItemBookingDto item = get(itemId,userId);
        Set<CommentDto> comments = new HashSet<>();
        comments.add(commentMapper.toCommentDto(comment));

        if (Objects.isNull(user) || Objects.isNull(item)) {
            throw new NotFoundException();
        }
        if (comment.getText().isEmpty() || comment.getText().isBlank()) {
            throw new ItemNotAvailableException();
        }
        if (!bookingRepository.existsBookingByItem_IdAndBooker_IdAndStatusAndEndIsBefore(
                itemId,userId, BookingStatus.APPROVED,Timestamp.valueOf(LocalDateTime.now()))) {
                throw new ItemNotAvailableException();
        }
        comment.setAuthor(userMapper.toEntity(user));
        comment.getAuthor().setName(user.getName());
        comment.setItem(mapper.toItemEntity(itemMapper.toItem(item)));
        comment.setText(comment.getText());
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        item.setComments(comments);
        return commentRepositoryMapper.toComment(commentRepository.save(commentRepositoryMapper.toEntity(comment)));
    }


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
        User user = userService.get(item.getOwner().getId());
        if (!itemOwnerId.equals(user.getId())) {
            throw new NotFoundException();
        }
    }
}

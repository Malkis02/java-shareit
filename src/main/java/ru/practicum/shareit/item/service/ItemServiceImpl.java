package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingRepositoryMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.mapper.CommentRepositoryMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemRepositoryMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.model.User;
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

    private final CommentRepositoryMapper commentMapper;

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
//        ItemEntity item = itemRepository.findById(itemId).orElseThrow();
//        ItemBookingDto itemBookingDto = itemMapper.toItemBookingDto(item);
//        if (item.getOwner().getId().equals(userId)) {
//            BookingShortDto lastBooking = bookingRepositoryMapper.toLastBookingDto(
//                    bookingRepository.findFirstByItemId_AndAndStartIsBeforeOrderByStartDesc(
//                            itemId,Timestamp.valueOf(LocalDateTime.now())));
//            BookingShortDto nextBooking = bookingRepositoryMapper.toLastBookingDto(
//                    bookingRepository.findFirstByItemId_AndStartIsAfterOrderByStartAsc(
//                            itemId,Timestamp.valueOf(LocalDateTime.now())));
//            itemBookingDto.setLastBooking(lastBooking);
//            itemBookingDto.setNextBooking(nextBooking);
//        }
        return itemRepository.findById(itemId)
                .map(itemMapper::toItemBookingDto)
                .orElseThrow(NotFoundException::new);
//        return itemBookingDto;
    }

    @Override
    public List<ItemBookingDto> getAll(Long userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(itemMapper::toItemBookingDto)
                .collect(Collectors.toList());
    }

    public Comment createComment(Comment comment,Long itemId,Long userId) {
        User user = userService.get(userId);
        Item item = itemMapper.toItem(get(itemId,userId));
        Set<CommentEntity> comments = new HashSet<>();
        comments.add(commentMapper.toEntity(comment));

        if (Objects.isNull(user) && Objects.isNull(item)) {
            throw new NotFoundException();
        }
        if (comment.getText().isEmpty() && comment.getText().isBlank()) {
            throw new ItemNotAvailableException();
        }
        if (Objects.isNull(item.getLastBooking()) && Objects.isNull(item.getNextBooking())) {
            throw new ItemNotAvailableException();
        }
        comment.setAuthor(userMapper.toEntity(user));
        comment.getAuthor().setName(user.getName());
        comment.setItem(mapper.toItemEntity(item));
        comment.setText(comment.getText());
        comment.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        item.setComments(comments);
        return commentMapper.toComment(commentRepository.save(commentMapper.toEntity(comment)));
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

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.entity.CommentEntity;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemEntity create(ItemEntity item, Long userId);

    ItemEntity update(ItemEntity item, Long itemId);

    ItemBookingDto get(Long itemId, Long userId);

    List<ItemBookingDto> getAll(Long userId);

    List<ItemEntity> search(String text);

    Comment createComment(Comment comment, Long itemId, Long userId);

    ItemEntity getItem(Long itemId);


}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.ItemEntity;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemEntity create(ItemDto item, Long userId);

    ItemEntity update(ItemEntity item, Long itemId);

    ItemEntity get(Long itemId, Long userId);

    List<ItemEntity> getAll(Long userId, int from, int size);

    List<ItemEntity> search(String text, int from, int size);

    Comment createComment(Comment comment, Long itemId, Long userId);

    ItemEntity getItem(Long itemId);


}

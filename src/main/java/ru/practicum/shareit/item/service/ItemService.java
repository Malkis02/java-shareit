package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(Item item,Long userId);

    Item update(Item item, Long itemId);

    ItemBookingDto get(Long itemId, Long userId);

    List<ItemBookingDto> getAll(Long userId);

    List<Item> search(String text);

    Comment createComment(Comment comment, Long itemId, Long userId);

    Item getItem(Long itemId);


}

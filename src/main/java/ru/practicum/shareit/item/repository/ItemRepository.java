package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Item item);
    Item update(Item item);
    Item get(Long itemId);
    List<Item> getAll(Long userId);
    List<Item> search(String s,Long userId);
    Boolean checkItem(Long userId,Long itemId);
}

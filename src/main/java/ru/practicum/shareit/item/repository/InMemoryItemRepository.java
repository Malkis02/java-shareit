package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IdValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> itemById = new HashMap<>();
    Long curId = 1L;

    @Override
    public Item create(Item item) {
        item.setId(curId);
        itemById.put(item.getId(), item);
        curId++;
        return item;
    }

    @Override
    public Item update(Item item, Long itemId) {
        Item savedItem = itemById.get(itemId);
        if (Objects.isNull(savedItem)) {
            throw new IdValidationException("Вещь отсутствует");
        }
        if (Objects.nonNull(item.getName()) && !Objects.equals(savedItem.getName(), item.getName())) {
            savedItem.setName(item.getName());
        }
        if (Objects.nonNull(item.getDescription()) && !Objects.equals(savedItem.getDescription(), item.getDescription())) {
            savedItem.setDescription(item.getDescription());
        }
        if (Objects.nonNull(item.getAvailable()) && !Objects.equals(savedItem.getAvailable(), item.getAvailable())) {
            savedItem.setAvailable(item.getAvailable());
        }
        return savedItem;
    }

    @Override
    public Item get(Long itemId) {
        if (itemById.containsKey(itemId)) {
            return itemById.get(itemId);
        } else {
            log.info("Вещь с id= {} отсутсвует", itemId);
            throw new IdValidationException("Вещь отсутсвует");
        }
    }

    @Override
    public List<Item> getAll(Long userId) {
        return itemById.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String s) {
        List<Item> items = itemById.values()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(s.toLowerCase()) ||
                        item.getName().toLowerCase().contains(s.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        if (items.size() != 0) {
            return items;
        } else {
            return Collections.emptyList();
        }
    }

    public Boolean checkItem(Long userId, Long itemId) {
        return itemById.get(itemId).getOwner().getId().equals(userId);
    }
}

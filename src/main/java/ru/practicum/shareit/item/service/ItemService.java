package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    private final UserService userService;


    public Item create(Item item) {
        List<User> users = userService.getAll();
        if (users.contains(userService.get(item.getOwner().getId()))) {
            return itemRepository.create(item);
        } else {
            throw new NotFoundException("Пользователя с таким id нет");
        }
    }

    public Item update(Item item, Long itemId) {
        validate(itemId, item);
        return itemRepository.update(item, itemId);
    }

    public Item get(Long itemId) {
        return itemRepository.get(itemId);
    }

    public List<Item> getAll(Long userId) {
        return itemRepository.getAll(userId);
    }

    public List<Item> search(String name) {
        if (!StringUtils.hasText(name)) {
            return Collections.emptyList();
        }
        return itemRepository.search(name);
    }

    private void validate(Long itemId, Item item) {
        if (!itemRepository.checkItem(item.getOwner().getId(), itemId)) {
            throw new NotFoundException("gsdgg");
        }
    }
}

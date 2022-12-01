package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collections;
import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item create(Item item){
        return itemRepository.create(item);
    }

    public Item update(Item item,Long itemId){
        validate(itemId,item);
        return itemRepository.update(item);
    }

    public Item get(Long itemId){
        return itemRepository.get(itemId);
    }

    public List<Item> getAll(Long userId){
        return itemRepository.getAll(userId);
    }

    public List<Item> search(String name,Long userId){
        if(!StringUtils.hasText(name)){
            return Collections.emptyList();
        }
        return itemRepository.search(name,userId);
    }

    private void validate(Long itemId,Item item){
        if(itemRepository.checkItem(item.getOwner().getId(),itemId)){
            throw new NotFoundException();
        }
    }
}

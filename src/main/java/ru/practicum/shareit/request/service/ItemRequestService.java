package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.entity.ItemRequestEntity;

import java.util.List;

public interface ItemRequestService {

    ItemRequestEntity create(ItemRequestEntity entity, Long userId);

    ItemRequestEntity get(Long userId,Long requestId);

    List<ItemRequestEntity> getAll(Long userId, int from, int size);

    List<ItemRequestEntity> getAll(Long userId);
}

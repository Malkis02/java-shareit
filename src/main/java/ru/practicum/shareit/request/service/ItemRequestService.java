package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.entity.ItemRequestEntity;

public interface ItemRequestService {

    ItemRequestEntity create(ItemRequestEntity entity, Long userId);

    ItemRequestEntity get(Long userId,Long requestId);
}

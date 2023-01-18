package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.entity.ItemRequestEntity;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;

    private final UserService userService;

    @Override
    public ItemRequestEntity create(ItemRequestEntity entity, Long userId) {
        UserEntity user = userService.get(userId);
        entity.setCreated(LocalDateTime.now());
        return repository.save(entity);
    }

    @Override
    public ItemRequestEntity get(Long userId,Long requestId) {
        UserEntity user = userService.get(userId);
        return repository.findById(requestId).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<ItemRequestEntity> getAll(Long userId, int from, int size) {
        return repository.findItemRequestEntitiesByRequestor_IdIsNot(userId, PageRequest.of((from / size),size));
    }

    @Override
    public List<ItemRequestEntity> getAll(Long userId) {
        UserEntity user = userService.get(userId);
        return repository.findAllByRequestorIdOrderByCreatedDesc(userId);
    }


}

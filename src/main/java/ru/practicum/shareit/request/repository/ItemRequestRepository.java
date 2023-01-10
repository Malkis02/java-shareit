package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.entity.ItemRequestEntity;

public interface ItemRequestRepository extends JpaRepository<ItemRequestEntity,Long> {
}

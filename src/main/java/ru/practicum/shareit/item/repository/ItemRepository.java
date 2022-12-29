package ru.practicum.shareit.item.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.entity.ItemEntity;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity,Long> {


    List<ItemEntity> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);

    List<ItemEntity> findAllByOwnerId(Long userId);

}

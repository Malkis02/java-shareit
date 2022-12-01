package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository {
    User create(User user);
    User update(User user,Long userId);
    User get(Long userId);
    void delete(Long userId);
    List<User> getAll();
}

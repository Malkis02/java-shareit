package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        return userRepository.create(user);
    }

    public User update(User user, Long userid) {
        userRepository.update(user, userid);
        user.setId(userid);
        return user;
    }

    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    public User get(Long userId) {
        return userRepository.get(userId);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }
}

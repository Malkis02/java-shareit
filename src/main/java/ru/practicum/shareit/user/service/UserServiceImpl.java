package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserRepositoryMapper mapper;

    @Override
    @Transactional
    public UserEntity create(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserEntity update(UserEntity user,Long userId) {
        UserEntity stored = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        mapper.updateEntity(user,stored);
        return userRepository.save(stored);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserEntity get(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<UserEntity> getAll() {
        return new ArrayList<>(userRepository.findAll());
    }
}

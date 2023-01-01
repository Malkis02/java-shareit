package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.entity.UserEntity;
import ru.practicum.shareit.user.mapper.UserRepositoryMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRepositoryMapper mapper;

    @Override
    public User create(User user) {
        return mapper.toUser(userRepository.save(mapper.toEntity(user)));
    }

    @Override
    @Transactional
    public User update(User user,Long userId) {
        UserEntity stored = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        mapper.updateEntity(user,stored);
        return mapper.toUser(userRepository.save(stored));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public User get(Long userId) {
        return userRepository.findById(userId)
                .map(mapper::toUser)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll().stream()
                .map(mapper::toUser)
                .collect(Collectors.toList());
    }
}

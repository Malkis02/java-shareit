package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.IdValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository{

    private final Map<Long,User> userById = new HashMap<>();

    Long curId = 1L;

    @Override
    public User create(User user) {
        for(User email: userById.values()) {
            if (email.getEmail().contains(user.getEmail())) {
                throw new EmailAlreadyExistException("Email занят");
            }
        }
        user.setId(curId);
        userById.put(user.getId(),user);
        curId++;
        return user;
    }

    @Override
    public User update(User user,Long userId) {
        User savedUser = userById.get(userId);
        List<String> userEmail = userById.values()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        if(userEmail.contains(user.getEmail())){
            throw new EmailAlreadyExistException("Email занят");
        }
        if(Objects.isNull(savedUser)) {
            throw new IdValidationException("Юзер отсутсвует");
        }
        if(user.getName()==null){
            user.setName(savedUser.getName());
        }
        if(user.getEmail()==null){
            user.setEmail(savedUser.getEmail());
        }
        userById.remove(savedUser.getId());
        userById.put(userId,user);
        return user;
    }

    @Override
    public User get(Long userId) {
        return userById.get(userId);
    }

    @Override
    public void delete(Long userId) {
        userById.remove(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userById.values());
    }
}

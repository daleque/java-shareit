package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistsElementException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
    public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getAll() {
        return repository.findAll();
        }

    @Override
    public User getById(long id) {
        return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        }

    @Override
    public User create(User user) {
        log.info("Пользователь добавлен {}", user);
        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ExistsElementException("Email используется");
        }
    }

    @Override
    public void remove(long id) {
        repository.findById(id);
        repository.deleteById(id);
        }

    @Override
    public User update(long id, User user) {
        User updatedUser = getValidUser(id, user);
        updatedUser.setId(id);
        log.info("Пользователь обновлен {}", updatedUser);

        return repository.save(updatedUser);
    }

    private User getValidUser(long userId, User user) {
        User updatedUser = repository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь не найден"));

        String updatedName = user.getName();
        if (updatedName != null && !updatedName.isBlank())
            updatedUser.setName(updatedName);

        String updatedEmail = user.getEmail();
        if (updatedEmail != null && !updatedEmail.isBlank()) {

            repository.findByEmailContainsIgnoreCase(updatedEmail).ifPresent(u -> {
                throw new ExistsElementException("Электронная почта существует");
            });
            updatedUser.setEmail(updatedEmail);
        }
        return updatedUser;
    }

    }
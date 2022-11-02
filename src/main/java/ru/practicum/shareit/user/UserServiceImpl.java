package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
    public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
            this.userStorage = userStorage;
        }

    @Override
    public Collection<User> getAll() {
            return userStorage.getAll();
        }

    @Override
    public User getById(int id) {
            return userStorage.getById(id);
        }

    @Override
    public User create(User user) {
        userStorage.getByEmail(user.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Пользователь с id %d не найден", user.getId()));
        });
        return userStorage.create(user);
    }

    @Override
    public void remove(int id) {
            userStorage.remove(id);
        }

    @Override
    public void removeAll() {
            userStorage.removeAll();
        }

    @Override
    public User update(int id, User user) {
        User updatedUser = userStorage.getById(id);
        String updatedName = user.getName();
        if (updatedName != null && !updatedName.isBlank()) {
            updatedUser.setName(updatedName);
        }
        String updatedEmail = user.getEmail();

        if (updatedEmail != null && !updatedEmail.isBlank()) {
            userStorage.getByEmail(updatedEmail).ifPresent(u -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Пользователь с id %d не найден", id));
            });
            updatedUser.setEmail(updatedEmail);
        }
        return userStorage.update(id, updatedUser);
    }

    }
package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAll();
    User getById(int id);
    User create(User user);
    void remove(int id);
    void removeAll();
    User update(int id, User user);
}

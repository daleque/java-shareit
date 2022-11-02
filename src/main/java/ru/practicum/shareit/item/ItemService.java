package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    Collection<Item> getAll();
    Item create(Item item);
    Item getById(int id);
    void remove(int id);
    Item update(int id, Item item);
    void removeAll();
    List<Item> searchByText(String text);
    List<Item> getAllByUser(int userId);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(long userId, Item item);

    Item getById(long id);

    Item update(long userId, long itemId, Item item);

    List<Item> searchByText(String text, int from, int size);

    List<Item> getAllByUser(long userId, int from, int size);

    Comment addCommentToItem(Comment comment);

    List<Comment> findCommentsByItemId(long itemId);

    List<Item> findByRequestId(long requestId);
}

package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item create(long userId, Item item) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        item.setOwner(owner);
        log.info("Предмет создан {}", item);
        return itemRepository.save(item);
    }

    @Override
    public Item getById(long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Предмет не найден"));
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        Item updatedItem = getValidItemDto(userId, itemId, item);
        updatedItem.setId(itemId);
        log.info("Предмет обновлен {}", updatedItem);
        return itemRepository.save(updatedItem);
    }

    @Override
    public List<Item> searchByText(String text, int from, int size) {
        if (text != null && !text.isBlank())
            return itemRepository.findByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseAndAvailableTrue(text,
                    text, PageRequest.of(from, size));
        return new ArrayList<>();
    }

    @Override
    public List<Item> getAllByUser(long userId, int from, int size) {
        return itemRepository.findByOwner_Id(userId, PageRequest.of(from, size, Sort.by("id")));
    }

    @Override
    public Comment addCommentToItem(Comment comment) {

        if (comment.getText() == null || comment.getText().isBlank())
            throw new ValidationException("Текст пустой");

        List<Booking> bookingList = bookingRepository.findByBooker_IdAndEndBefore(comment.getAuthor().getId(),
                LocalDateTime.now(), PageRequest.of(0, 10));
        if (bookingList.isEmpty())
            throw new ValidationException("Пользователь не бронирует свой товар");
        comment.setCreated(LocalDateTime.now());
        log.info("Комментарий добавлен {}", comment);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findCommentsByItemId(long itemId) {
        return commentRepository.findByItem_IdOrderByCreatedDesc(itemId);
    }

    private Item getValidItemDto(long userId, long itemId, Item item) {
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(
                () -> new ObjectNotFoundException("Предмен не найден"));

        if (userRepository.findById(userId).isPresent() && !updatedItem.getOwner().getId().equals(userId))
            throw new ObjectNotFoundException("Access denied");

        String updatedName = item.getName();
        if (updatedName != null && !updatedName.isBlank())
            updatedItem.setName(updatedName);

        String updatedDescription = item.getDescription();
        if (updatedDescription != null && !updatedDescription.isBlank()) {
            updatedItem.setDescription(updatedDescription);
        }

        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return updatedItem;
    }

    @Override
    public List<Item> findByRequestId(long requestId) {
        return itemRepository.findByRequest_Id(requestId, Sort.by("id").descending());
    }

}
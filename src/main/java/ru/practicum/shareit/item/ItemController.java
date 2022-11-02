package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemServiceImpl itemServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto create(
            @RequestHeader(USER_ID_HEADER) int userId,
            @RequestBody ItemDto itemDto
    ) {
        Item item = itemMapper.toItem(itemDto, userServiceImpl.getById(userId));
        Item itemSaved = itemServiceImpl.create(item);
        return itemMapper.toItemDto(itemSaved);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader(USER_ID_HEADER) int userId,
            @PathVariable int itemId,
            @RequestBody ItemDto itemDto
    ) {
        Item itemValid = itemServiceImpl.getById(itemId);
        if (itemValid.getOwner().getId() != userId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещь не пренадлежит владельцу");
        }
        Item item = itemMapper.toItem(itemDto, userServiceImpl.getById(userId));
        Item itemUpdated = itemServiceImpl.update(itemId, item);
        return itemMapper.toItemDto(itemUpdated);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable int id) {
        return itemMapper.toItemDto(itemServiceImpl.getById(id));
    }

    @GetMapping
    public List<ItemDto> getAllByUser(@RequestHeader(USER_ID_HEADER) int userId) {
        return itemServiceImpl.getAllByUser(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchByText(@RequestParam String text) {
        return itemServiceImpl.searchByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
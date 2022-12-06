package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new ValidationException("Description is empty");
        }
        itemRequest.setCreated(LocalDateTime.now().withNano(0));
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> findByRequesterId(long requesterId) {
        userRepository.findById(requesterId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return itemRequestRepository.findByRequester_IdOrderByCreatedAsc(requesterId);
    }

    @Override
    public List<ItemRequest> findAll(long userId, int from, int size) {
        return itemRequestRepository.findByRequester_IdNot(
                userId,
                PageRequest.of(from, size, Sort.by("created").descending())
        );
    }

    @Override
    public ItemRequest findById(long id) {
        return itemRequestRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Request not found"));
    }
}

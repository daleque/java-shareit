package ru.practicum.shareit.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ExistsElementException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    private final User user = new User(1L, "Name", "test@test.ru");
    @Mock
    UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void shouldNoSuchElementExceptionByWrongUser() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Exception thrown = assertThrows(ObjectNotFoundException.class, () -> userService.update(1L, user));
        assertEquals("Пользователь не найден", thrown.getMessage());
    }

    @Test
    public void shouldExistsElementException() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.findByEmailContainsIgnoreCase(Mockito.anyString()))
                .thenReturn(Optional.of(user));
        User updateUser = new User(1L, "Name 2", "test@test.ru");
        Exception thrown = assertThrows(ExistsElementException.class, () -> userService.update(1L, updateUser));
        assertEquals("Электронная почта существует", thrown.getMessage());
    }

}

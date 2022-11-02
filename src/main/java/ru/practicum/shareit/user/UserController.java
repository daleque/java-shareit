package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userServiceImpl.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable int id) {
        return UserMapper.toUserDto(userServiceImpl.getById(id));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(
                userServiceImpl.create(user)
        );
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id, @RequestBody User user) {
        return UserMapper.toUserDto(userServiceImpl.update(id, user));
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable int id) {
        userServiceImpl.remove(id);
    }
}
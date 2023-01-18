package ru.practicum.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto addUser(@Validated @RequestBody NewUserRequest newUserRequest) {
        return userService.addUser(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "ids") List<Long> ids,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) int from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return userService.getUsers(ids, pageable);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}

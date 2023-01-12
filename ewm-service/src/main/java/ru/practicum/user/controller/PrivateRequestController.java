package ru.practicum.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.user.service.UserService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final UserService userService;

    @Autowired
    public PrivateRequestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public RequestDto addNewRequestIntoEventByUser(@Positive @PathVariable long userId,
                                                   @Positive @RequestParam(name = "eventId") long eventId) {
        return userService.addNewRequestIntoEventByUser(eventId, userId);
    }

    @GetMapping
    public List<RequestDto> getUserRequests(@Positive @PathVariable long userId) {
        return userService.getUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequestByCurrentUser(@Positive @PathVariable long userId,
                                                 @Positive @PathVariable long requestId) {
        return userService.cancelRequestByCurrentUser(userId, requestId);
    }
}

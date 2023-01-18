package ru.practicum.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentInDto;
import ru.practicum.comment.dto.CommentOutDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    @Autowired
    public PrivateEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventFullDto addNewEvent(@Positive @PathVariable long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventFullDto> getEventsCurrentUser(@Positive @PathVariable long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) int from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return eventService.getEventsByCurrentUser(userId, pageable);
    }

    @PatchMapping
    public EventFullDto updateEventByCurrentUser(@Positive @PathVariable long userId,
                                                 @Valid @RequestBody UpdateEventRequest updateEventRequest) {
        return eventService.updateEventByCurrentUser(userId, updateEventRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByCurrentUser(@Positive @PathVariable long userId,
                                              @Positive @PathVariable long eventId) {
        return eventService.getEventByCurrentUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto cancelEventByCurrentUser(@Positive @PathVariable long userId,
                                                 @Positive @PathVariable long eventId) {
        return eventService.cancelEventByCurrentUser(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getOtherRequestsByEventAndCurrentUser(@Positive @PathVariable long userId,
                                                                               @Positive @PathVariable long eventId) {
        return eventService.getOtherRequestsByEventAndCurrentUser(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmOtherRequestByEventByCurrentUser(@Positive @PathVariable long userId,
                                                                           @Positive @PathVariable long eventId,
                                                                           @Positive @PathVariable long reqId) {
        return eventService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectOtherRequestByEventByCurrentUser(@Positive @PathVariable long userId,
                                                                          @Positive @PathVariable long eventId,
                                                                          @Positive @PathVariable long reqId) {
        return eventService.rejectRequest(userId, eventId, reqId);
    }

    @PostMapping("/{eventId}/comments")
    public CommentOutDto addComment(@Positive @PathVariable long userId,
                                    @Positive @PathVariable long eventId,
                                    @Valid @RequestBody CommentInDto commentInDto) {
        return eventService.addComment(commentInDto, userId, eventId);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentOutDto updateComment(@Positive @PathVariable long userId,
                                       @Positive @PathVariable long commentId,
                                       @Valid @RequestBody CommentInDto commentInDto) {
        return eventService.updateComment(commentInDto, commentId, userId);
    }
}

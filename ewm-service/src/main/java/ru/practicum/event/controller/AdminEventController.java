package ru.practicum.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.service.EventService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/admin/events")

public class AdminEventController {
    private final EventService eventService;

    @Autowired
    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(name = "users", required = false) List<Long> usersId,
                                        @RequestParam(name = "states", required = false) List<String> states,
                                        @RequestParam(name = "categories", required = false) List<Long> categories,
                                        @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                        @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                        @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return eventService.getEventsByFilter(usersId, states, categories, rangeStart, rangeEnd, pageable);
    }

    @PutMapping("/{eventId}")
    public EventFullDto updateEvent(@Positive @PathVariable long eventId,
                                    @RequestBody AdminUpdateEventRequest adminUpdateEventRequest) {
        return eventService.updateEvent(eventId, adminUpdateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@Positive @PathVariable long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@Positive @PathVariable long eventId) {
        return eventService.rejectEvent(eventId);
    }

    @DeleteMapping("/comments/{commentId}")
    public void removeComment(@Positive @PathVariable long commentId) {
        eventService.removeComment(commentId);
    }
}
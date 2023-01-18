package ru.practicum.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentOutDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    @Autowired
    public PublicEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> getEventsByFilter(@RequestParam(name = "text", required = false) String text,
                                                @RequestParam(name = "categories", required = false, defaultValue = "") List<Long> categories,
                                                @RequestParam(name = "paid", required = false) boolean paid,
                                                @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                @RequestParam(name = "rangeEnf", required = false) String rangeEnd,
                                                @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") boolean onlyAvailable,
                                                @RequestParam(name = "sort", required = false) String sort,
                                                @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                HttpServletRequest request) {
        int page = from / size;
        Optional<EventSort> eventSort = EventSort.from(sort);
        Pageable pageable = eventSort.map(value -> PageRequest.of(page, size, Sort.by(value.name()).descending()))
                .orElseGet(() -> PageRequest.of(page, size));
        return eventService.getEventsByFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable @Positive long id, HttpServletRequest request) {
        return eventService.getEvent(id, request);
    }

    @GetMapping("/{eventId}/comments")
    public List<CommentOutDto> getComments(@Positive @PathVariable long eventId,
                                           @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                           @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        int page = from / size;
        return eventService.getComments(eventId, PageRequest.of(page, size));
    }

    @GetMapping("/comments/{commentId}")
    public CommentOutDto getCommentById(@Positive @PathVariable long commentId) {
        return eventService.getCommentById(commentId);
    }

    @GetMapping("/comments/search")
    public List<CommentOutDto> searchComments(@RequestParam String text,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        int page = from / size;
        return eventService.searchComments(text, PageRequest.of(page, size));
    }
}

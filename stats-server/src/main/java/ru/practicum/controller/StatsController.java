package ru.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.DtoRequestFilter;
import ru.practicum.model.ViewStats;
import ru.practicum.model.dto.EndPointHitDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@ControllerAdvice
public class StatsController {
    private final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false, defaultValue = "") List<String> uris,
                                    @RequestParam(required = false, defaultValue = "false") boolean unique) {
        DtoRequestFilter dtoRequestFilter =
                new DtoRequestFilter(LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), uris, unique);
        return statsService.getStats(dtoRequestFilter);
    }

    @PostMapping("/hit")
    public EndPointHitDto addHit(@RequestBody EndPointHitDto endPointHitDto) {
        return statsService.addHit(endPointHitDto);
    }
}
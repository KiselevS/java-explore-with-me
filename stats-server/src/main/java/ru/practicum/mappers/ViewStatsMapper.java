package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.model.EndPointHit;
import ru.practicum.model.ViewStats;

@Component
public class ViewStatsMapper {
    public static ViewStats toViewStats(EndPointHit endPointHit) {
        return ViewStats.builder()
                .app(endPointHit.getApp())
                .uri(endPointHit.getUri())
                .build();
    }
}

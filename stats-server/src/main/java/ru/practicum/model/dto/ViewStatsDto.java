package ru.practicum.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ViewStatsDto {
    private String app;

    private String uri;

    private long hits;
}
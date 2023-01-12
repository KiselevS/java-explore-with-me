package ru.practicum.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class ViewStats {
    private String app;

    private String uri;

    private long hits;
}
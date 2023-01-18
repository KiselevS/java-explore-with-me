package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    private Set<Long> events;

    @NotNull
    @Builder.Default
    private boolean pinned = false;

    @NotNull
    private String title;
}

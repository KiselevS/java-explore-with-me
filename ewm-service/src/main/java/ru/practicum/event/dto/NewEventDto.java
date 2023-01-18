package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.model.Location;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {
    @NotNull
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private long category;

    @NotNull
    @Length(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    @NotNull
    @Builder.Default
    private boolean paid = false;

    @Builder.Default
    private int participantLimit = 0;

    @NotNull
    private boolean requestModeration;

    @NotNull
    @Length(min = 3, max = 120)
    private String title;
}

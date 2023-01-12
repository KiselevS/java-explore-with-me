package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.request.model.RequestState;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();

    @Positive
    private long event;

    @Positive
    private long id;

    @Positive
    private long requester;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private RequestState status = RequestState.PENDING;
}

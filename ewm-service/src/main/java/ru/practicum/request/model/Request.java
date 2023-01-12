package ru.practicum.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "event_id")
    private long event;

    @Column(name = "requester_id")
    private long requester;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private RequestState status = RequestState.PENDING;
}

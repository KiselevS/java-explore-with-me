package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "stats")
public class EndPointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app", nullable = false, length = 32)
    private String app;

    @Column(name = "uri", nullable = false, length = 2048)
    private String uri;

    @Column(name = "ip", nullable = false, length = 32)
    private String ip;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "timestamp")
    private LocalDateTime timeStamp = LocalDateTime.now();
}
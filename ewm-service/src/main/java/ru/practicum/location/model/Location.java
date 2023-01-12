package ru.practicum.location.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lat")
    private float lat;

    @Column(name = "lon")
    private float lon;
}

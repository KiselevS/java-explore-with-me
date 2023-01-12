package ru.practicum.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.model.EndPointHit;
import ru.practicum.model.dto.EndPointHitDto;

@Component
public class EndPointMapper {

    public static EndPointHitDto toEndPointHitDto(EndPointHit endPointHit) {
        return EndPointHitDto.builder()
                .app(endPointHit.getApp())
                .uri(endPointHit.getUri())
                .ip(endPointHit.getIp())
                .timeStamp(endPointHit.getTimeStamp())
                .build();
    }

    public static EndPointHit toEndPointHit(EndPointHitDto endPointHit) {
        return EndPointHit.builder()
                .app(endPointHit.getApp())
                .uri(endPointHit.getUri())
                .ip(endPointHit.getIp())
                .timeStamp(endPointHit.getTimeStamp())
                .build();
    }
}

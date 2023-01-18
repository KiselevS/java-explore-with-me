package ru.practicum.request.mapper;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent())
                .id(request.getId())
                .requester(request.getRequester())
                .status(request.getStatus())
                .build();
    }

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent())
                .id(request.getId())
                .requester(request.getRequester())
                .status(request.getStatus())
                .build();
    }
}

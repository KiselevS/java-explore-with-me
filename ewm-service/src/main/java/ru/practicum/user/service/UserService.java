package ru.practicum.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.reposiroty.RequestRepository;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       EventRepository eventRepository,
                       RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);

        if (userRepository.findByName(newUserRequest.getName()).isPresent() ||
                userRepository.findByEmail(newUserRequest.getEmail()).isPresent()) {
            throw new ConflictException("User already exists");
        }
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        return userRepository.findAllByIdIn(ids, pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public RequestDto addNewRequestIntoEventByUser(long eventId, long userId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Failed to find user with id=%d", userId));
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("Event not published");
        } else if (event.getInitiator().getId() == userId) {
            throw new BadRequestException("Can't submit request for own event");
        } else if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new BadRequestException("Request limit has been reached");
        }

        Request request;
        Optional<Request> requestFound = requestRepository.findRequestByRequester(userId);

        if (requestFound.isPresent()) {
            throw new BadRequestException("Request already exists");
        } else {
            request = new Request();
        }

        request.setEvent(eventId);
        request.setRequester(userId);
        request.setCreated(LocalDateTime.now());
        if (!event.isRequestModeration()) {
            request.setStatus(RequestState.PENDING);
        }
        request = requestRepository.save(request);
        return RequestMapper.toRequestDto(request);
    }

    public List<RequestDto> getUserRequests(long userId) {
        return requestRepository.findRequestsByRequester(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public RequestDto cancelRequestByCurrentUser(long userId, long requestId) {

        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Failed to find user with id=%d", userId));
        }

        Request request = requestRepository.findRequestByRequester(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find request with id=%d", requestId)));

        request.setStatus(RequestState.CANCELED);

        request = requestRepository.save(request);
        return RequestMapper.toRequestDto(request);
    }
}

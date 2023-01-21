package ru.practicum.event.service;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.comment.dto.CommentInDto;
import ru.practicum.comment.dto.CommentOutDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestState;
import ru.practicum.request.reposiroty.RequestRepository;
import ru.practicum.statistic.client.EventClient;
import ru.practicum.statistic.dto.EndPointHitDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventClient eventClient;
    private final CommentRepository commentRepository;

    @Autowired
    public EventService(EventRepository eventRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        RequestRepository requestRepository,
                        EventClient eventClient,
                        CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.eventClient = eventClient;
        this.commentRepository = commentRepository;
    }


    public EventFullDto addEvent(long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Only pending or canceled events can be changed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find user with id=%d", userId)));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find category with id=%d",
                        newEventDto.getCategory())));

        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event = eventRepository.save(event);

        return EventMapper.toEventFullDto(event);
    }

    public List<EventFullDto> getEventsByCurrentUser(long userId, Pageable pageable) {
        return eventRepository.findEventsByInitiator_Id(userId, pageable).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public EventFullDto updateEventByCurrentUser(long userId, UpdateEventRequest updateEventRequest) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(updateEventRequest.getEventId(), userId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d",
                        updateEventRequest.getEventId())));

        if (updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Only pending or canceled events can be changed");
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new BadRequestException("Only pending or canceled events can be changed");
        }

        if (event.getState() == EventState.CANCELED) {
            event.setState(EventState.PENDING);
        }

        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Failed to find category with id=%d",
                            updateEventRequest.getCategory())));
            event.setCategory(category);
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }

        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    public EventFullDto getEventByCurrentUser(long userId, long eventId) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));
        return EventMapper.toEventFullDto(event);
    }

    public EventFullDto cancelEventByCurrentUser(long userId, long eventId) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only required moderation events can be changed");
        }

        event.setState(EventState.CANCELED);

        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }


    public List<ParticipationRequestDto> getOtherRequestsByEventAndCurrentUser(long userId, long eventId) {
        if (eventRepository.findEventByIdAndInitiator_Id(eventId, userId).isEmpty()) {
            throw new NotFoundException(String.format("Failed to find event with id=%d", eventId));
        }
        return requestRepository.findRequestsByEvent(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto confirmRequest(long userId, long eventId, long reqId) {
        Event event = eventRepository.findEventByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        if (event.getConfirmedRequests() == event.getParticipantLimit()) {
            List<Request> requests = requestRepository.findRequestsByEventAndStatus(eventId, RequestState.PENDING).stream()
                    .peek(request -> request.setStatus(RequestState.REJECTED))
                    .collect(Collectors.toList());
            requestRepository.saveAll(requests);
            throw new BadRequestException(String.format("Request limit for event with id=%d has been exhausted", eventId));
        }
        long confirmed = event.getConfirmedRequests();
        event.setConfirmedRequests(++confirmed);
        eventRepository.save(event);
        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find request with id=%d", reqId)));
        request.setStatus(RequestState.CONFIRMED);
        request = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }


    public ParticipationRequestDto rejectRequest(long userId, long eventId, long reqId) {
        if (eventRepository.findEventByIdAndInitiator_Id(eventId, userId).isEmpty()) {
            throw new NotFoundException(String.format("Failed to find event with id=%d", eventId));
        }

        Request request = requestRepository.findById(reqId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find request with id=%d", reqId)));

        request.setStatus(RequestState.REJECTED);
        request = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }


    public List<EventFullDto> getEventsByFilter(List<Long> usersId,
                                                List<String> states,
                                                List<Long> categories,
                                                String rangeStart,
                                                String rangeEnd,
                                                Pageable pageable) {

        QEvent e = QEvent.event;
        List<Predicate> predicates = new ArrayList<>();

        if (!usersId.isEmpty()) {
            predicates.add(e.initiator.id.in(usersId));
        }
        if (states != null) {
            predicates.add(e.state.in(states.stream().map(EventState::valueOf).collect(Collectors.toList())));
        }
        if (!categories.isEmpty()) {
            predicates.add((e.category.id.in(categories)));
        }

        if (rangeStart != null) {
            predicates.add(e.eventDate.after(LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        } else predicates.add(e.eventDate.after(LocalDateTime.now()));

        if (rangeEnd != null) {
            predicates.add(e.eventDate.before(LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }

        Page<Event> events = eventRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicates)), pageable);

        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }


    public EventFullDto updateEvent(long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        event.setAnnotation(adminUpdateEventRequest.getAnnotation());

        Category category = categoryRepository.findById(adminUpdateEventRequest.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find category with id=%d",
                        adminUpdateEventRequest.getCategory())));
        event.setCategory(category);

        event.setDescription(adminUpdateEventRequest.getDescription());
        event.setEventDate(adminUpdateEventRequest.getEventDate());
        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLocation(adminUpdateEventRequest.getLocation());
        }
        event.setPaid(adminUpdateEventRequest.isPaid());
        event.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());
        event.setRequestModeration(adminUpdateEventRequest.isRequestModeration());
        event.setTitle(adminUpdateEventRequest.getTitle());

        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }


    public EventFullDto publishEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Start date of event must be no earlier than one hour from the date of publication");
        }

        event.setState(EventState.PUBLISHED);
        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }


    public EventFullDto rejectEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        if (event.getState() == EventState.PUBLISHED) {
            throw new BadRequestException("Can't reject published event");
        }

        event.setState(EventState.CANCELED);
        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    public List<EventFullDto> getEventsByFilter(String text,
                                                List<Long> categories,
                                                Boolean paid,
                                                String rangeStart,
                                                String rangeEnd,
                                                Boolean onlyAvailable,
                                                Pageable pageable,
                                                HttpServletRequest request) {

        QEvent e = QEvent.event;
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(e.state.eq(EventState.PUBLISHED));

        if (text != null) {
            predicates.add(e.annotation.likeIgnoreCase(text).or(e.description.likeIgnoreCase(text)));
        }
        if (!categories.isEmpty()) {
            predicates.add(e.category.id.in(categories));
        }
        if (paid != null) {
            predicates.add(e.paid.eq(paid));
        }
        if (rangeStart != null) {
            predicates.add(e.eventDate.after(LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        } else predicates.add(e.eventDate.after(LocalDateTime.now()));

        if (onlyAvailable) {
            predicates.add(e.confirmedRequests.lt(e.participantLimit));
        }

        if (rangeEnd != null) {
            predicates.add(e.eventDate.before(LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }

        List<Event> events = (List<Event>) eventRepository.findAll(Objects.requireNonNull(ExpressionUtils.allOf(predicates)));

        for (Event event : events) {
            long views = event.getViews() + 1;
            event.setViews(views);
        }

        EndPointHitDto endPointHitDto = new EndPointHitDto().toBuilder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .build();
        eventClient.addHit(endPointHitDto);

        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    public EventFullDto getEvent(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        EndPointHitDto endPointHitDto = new EndPointHitDto().toBuilder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .build();
        eventClient.addHit(endPointHitDto);
        long views = event.getViews() + 1;
        event.setViews(views);
        return EventMapper.toEventFullDto(event);
    }


    public CommentOutDto addComment(CommentInDto commentInDto, long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        if (event.getState() != EventState.PUBLISHED) {
            throw new BadRequestException("Can't comment unpublished event");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find user with id=%d", userId)));

        Comment comment = Comment.builder()
                .text(commentInDto.getText())
                .author(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();

        comment = commentRepository.save(comment);
        return CommentMapper.toCommentOutDto(comment);
    }

    public CommentOutDto updateComment(CommentInDto commentInDto, long commentId, long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find comment with id=%d", commentId)));

        if (comment.getAuthor().getId() != userId) {
            throw new BadRequestException("Only your own comment can be changed");
        }

        if (comment.getCreated().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new BadRequestException("You can update comment within 24 hours");
        }

        comment.setText(commentInDto.getText());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentOutDto(comment);
    }

    public void removeComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentOutDto> getComments(long eventId, Pageable pageable) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));

        if (event.getState() != EventState.PUBLISHED) {
            throw new BadRequestException("Can't get comments for unpublished event");
        }

        return commentRepository.findByEvent_Id(eventId, pageable).stream()
                .map(CommentMapper::toCommentOutDto).collect(Collectors.toList());
    }

    public CommentOutDto getCommentById(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find comment with id=%d", commentId)));

        Event event = eventRepository.findById(comment.getEvent().getId())
                .orElseThrow(() -> new NotFoundException("Failed to find event"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new BadRequestException("Can't get comment for unpublished event");
        }

        return CommentMapper.toCommentOutDto(comment);
    }

    public List<CommentOutDto> searchComments(String text, Pageable pageable) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return commentRepository.findByTextContainsIgnoreCaseAndEvent_State(text, EventState.PUBLISHED, pageable)
                .stream().map(CommentMapper::toCommentOutDto).collect(Collectors.toList());
    }
}

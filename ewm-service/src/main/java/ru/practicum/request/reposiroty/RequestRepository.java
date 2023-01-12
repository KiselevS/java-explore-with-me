package ru.practicum.request.reposiroty;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findRequestsByEvent(long eventId);

    Optional<Request> findRequestByRequester(long userId);

    List<Request> findRequestsByRequester(long userId);
}

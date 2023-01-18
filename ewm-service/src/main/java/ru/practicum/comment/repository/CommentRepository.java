package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.EventState;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTextLikeIgnoreCaseAndEvent_State(String text, EventState state, Pageable pageable);

    List<Comment> findByEvent_Id(Long id, Pageable pageable);
}

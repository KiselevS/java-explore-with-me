package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.EndPointHit;

public interface StatsRepository extends JpaRepository<EndPointHit, Long>, QuerydslPredicateExecutor<EndPointHit> {
    Long countByApp(String name);
}
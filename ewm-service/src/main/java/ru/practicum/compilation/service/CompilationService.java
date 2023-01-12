package ru.practicum.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.excption.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CompilationService(CompilationRepository compilationRepository,
                              EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }


    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList = eventRepository.findAllById(newCompilationDto.getEvents());

        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventList);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    public void deleteCompilationById(long compId) {
        if (compilationRepository.findById(compId).isEmpty()) {
            throw new NotFoundException(String.format("Failed to find compilation with id=%d", compId));
        }
        compilationRepository.deleteById(compId);
    }


    public void deleteEventFromCompilation(long compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find compilation with id=%d", compId)));

        List<Event> eventList = compilation.getEvents().stream()
                .filter(event -> event.getId() != eventId)
                .collect(Collectors.toList());
        compilation.setEvents(eventList);
        compilationRepository.save(compilation);
    }


    public CompilationDto addEventIntoCompilation(long compId, long eventId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find compilation with id=%d", compId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find event with id=%d", eventId)));
        compilation.getEvents().add(event);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }


    public CompilationDto unpinCompilationFromMainPage(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find compilation with id=%d", compId)));
        compilation.setPinned(false);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    public CompilationDto pinCompilationOnMainPage(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find compilation with id=%d", compId)));
        compilation.setPinned(true);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    public List<CompilationDto> getCompilations(boolean pinned, Pageable pageable) {
        return compilationRepository.findAll(pageable).stream()
                .filter(compilation -> compilation.isPinned() == pinned)
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }


    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find compilation with id=%d", compId)));
        return CompilationMapper.toCompilationDto(compilation);
    }
}

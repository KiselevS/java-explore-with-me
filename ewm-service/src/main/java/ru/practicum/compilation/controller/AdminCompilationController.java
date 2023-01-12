package ru.practicum.compilation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @Autowired
    public AdminCompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@Positive @PathVariable long compId) {
        compilationService.deleteCompilationById(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@Positive @PathVariable long eventId,
                                           @Positive @PathVariable long compId) {
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public CompilationDto addEventIntoCompilation(@Positive @PathVariable long compId,
                                                  @Positive @PathVariable long eventId) {
        return compilationService.addEventIntoCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public CompilationDto unpinCompilationFromMainPage(@Positive @PathVariable long compId) {
        return compilationService.unpinCompilationFromMainPage(compId);
    }

    @PatchMapping("/{compId}/pin")
    public CompilationDto pinCompilationOnMainPage(@Positive @PathVariable long compId) {
        return compilationService.pinCompilationOnMainPage(compId);
    }

}

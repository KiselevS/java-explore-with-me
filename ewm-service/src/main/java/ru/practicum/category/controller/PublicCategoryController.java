package ru.practicum.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    @Autowired
    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0", required = false) int from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.getCategories(pageable);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable @Positive long catId) {
        return categoryService.getCategoryById(catId);
    }

}

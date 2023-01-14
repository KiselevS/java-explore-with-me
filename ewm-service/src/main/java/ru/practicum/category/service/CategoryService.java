package ru.practicum.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);

        if (categoryRepository.findByName(newCategoryDto.getName()).isPresent()) {
            throw new ConflictException("Category already exists");
        }

        category = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new BadRequestException(
                        String.format("Category with id=%d was not found.", categoryDto.getId())));

        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new ConflictException("Category already exists");
        }

        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }


    public void deleteCategory(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find category with id=%d", catId)));
        categoryRepository.delete(category);
    }

    public List<CategoryDto> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Failed to find category with id=%d", catId)));

        return CategoryMapper.toCategoryDto(category);
    }
}
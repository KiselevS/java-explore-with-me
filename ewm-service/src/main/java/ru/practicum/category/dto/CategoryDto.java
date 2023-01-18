package ru.practicum.category.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@Builder
public class CategoryDto {
    @NotNull
    @PositiveOrZero
    private long id;

    @NotBlank
    private String name;
}

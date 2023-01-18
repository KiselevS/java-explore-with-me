package ru.practicum.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentInDto {
    @NotBlank
    private String text;
}

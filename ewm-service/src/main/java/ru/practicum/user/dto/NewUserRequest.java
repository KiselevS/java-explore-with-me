package ru.practicum.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewUserRequest {
    @NotBlank
    private String name;

    @Email
    @NotNull
    private String email;
}

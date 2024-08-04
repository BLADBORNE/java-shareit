package ru.practicum.shareit.user.model;

import lombok.Getter;
import ru.practicum.shareit.user.constraint.CreatedUser;
import ru.practicum.shareit.user.constraint.UpdatedUser;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class User {
    private Integer id;
    @NotBlank(groups = CreatedUser.class)
    @Pattern(regexp = ".*[^ ].*", groups = UpdatedUser.class)
    private String name;
    @Email(groups = {CreatedUser.class, UpdatedUser.class})
    @NotBlank(groups = CreatedUser.class)
    private String email;
}
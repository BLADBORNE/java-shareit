package ru.practicum.shareit.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.constraint.CreatedUser;
import ru.practicum.shareit.user.constraint.UpdatedUser;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
@Validated
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(groups = CreatedUser.class)
    @Pattern(regexp = ".*[^ ].*",groups = UpdatedUser.class)
    private String name;
    @Email(groups = {CreatedUser.class, UpdatedUser.class})
    @NotBlank(groups = CreatedUser.class)
    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
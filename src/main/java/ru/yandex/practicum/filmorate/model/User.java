package ru.yandex.practicum.filmorate.model;

import lombok.*;
import jakarta.validation.constraints.*;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;

@Entity
@Data
public class User {
    private int id;

    @NotEmpty(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    @NotEmpty(message = "Логин не может быть пустым")
    @NotBlank(message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthday;

    @PrePersist
    @PreUpdate
    private void setDisplayNameIfEmpty() {
        if (name == null || name.trim().isEmpty()) {
            name = login;
        }
        System.out.println("Method setDisplayNameIfEmpty called");
    }
}

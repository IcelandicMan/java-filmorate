package ru.yandex.practicum.filmorate.model;

import lombok.*;
import javax.validation.constraints.*;

import java.time.LocalDate;

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
}

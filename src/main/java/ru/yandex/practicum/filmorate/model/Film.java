package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotEmpty(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ValidReleaseDate(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
}

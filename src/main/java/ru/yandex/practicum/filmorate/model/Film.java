package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ValidReleaseDate(message = "Дата релиза должна быть не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;


    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    private Mpa mpa;

    private int rate;

    private List<Genre> genres = new ArrayList<>();

    public Film() {
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, int rate, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
    }
}

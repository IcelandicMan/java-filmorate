package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final List<Film> films = new ArrayList<>();
    private int nextFilmId = 1;

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрошен список фильмов");
        return films;
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        if (hasValidationErrors(film)) {
            throw new ValidationException("Ошибка валидации фильма");
        }
        film.setId(nextFilmId++);
        films.add(film);
        log.info("Фильм добавлен: {} ", film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        if (hasValidationErrors(updatedFilm)) {
            throw new ValidationException("Ошибка валидации фмльма");
        }
        int id = updatedFilm.getId();
        for (int i = 0; i < films.size(); i++) {
            Film film = films.get(i);
            if (film.getId() == id) {
                if (updatedFilm.getName() != null) {
                    film.setName(updatedFilm.getName());
                }
                if (updatedFilm.getDescription() != null) {
                    film.setDescription(updatedFilm.getDescription());
                }
                if (updatedFilm.getReleaseDate() != null) {
                    film.setReleaseDate(updatedFilm.getReleaseDate());
                }
                if (updatedFilm.getDuration() > 0) {
                    film.setDuration(updatedFilm.getDuration());
                }
                log.info("Фильм под ID {} обновлен: {} ", id, film);
                return film;
            }
        }
        log.warn("Фильм под ID {} не найден", id);
        throw new ValidationException("Фильм с данным ID не найден");
    }

    private boolean hasValidationErrors(Film film) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        return !violations.isEmpty();
    }
}
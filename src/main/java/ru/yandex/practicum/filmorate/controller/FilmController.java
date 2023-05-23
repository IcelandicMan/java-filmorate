package ru.yandex.practicum.filmorate.controller;

import javax.validation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final static LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);
    ;
    Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 0;

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрошен список фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("создание фильма: {} ", film);
        isValid(film);
        film.setId(++idCounter);
        films.put(film.getId(), film);
        log.info("Фильм добавлен: {} ", film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info("Обновление фильма: {} ", updatedFilm);
        isValid(updatedFilm);
        int id = updatedFilm.getId();
        if (films.containsKey(id)) {
            Film film = films.get(id);
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

    private void isValid(Film film) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации пользователя");
        }
        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            log.warn("Дата фильма ранее 28 декабря 1895");
            throw new ValidationException("Ошибка валидации пользователя");
        }
    }
}
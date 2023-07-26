package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Запрошен фильм с id: {} ", id);
        Film film = filmService.getFilm(id);
        log.info("Запрос на фильм с id {} выполнен: {} ", film.getId(), film);
        return film;
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на создание фильма: {} ", film);
        Film createdFilm = filmService.createFilm(film);
        log.info("Запрос на создание фильма выполнен. Фильм создан: {} ", createdFilm);
        return createdFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрошен список фильмов");
        List<Film> films = filmService.getFilms();
        log.info("Запрос на предоставление списка всех фильмов выполнен");
        return films;
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        log.info("Запрошен список из {} популярных фильмов с жанром под id {} и {} годом выпуска",
                count, genreId, year);
        List<Film> popularFilms = filmService.getFilmsByLikes(count, genreId, year);
        log.info("Запрос списка из {} популярных фильмов с жанром под id {} и {} годом выпуска выполнен",
                count, genreId, year);
        return popularFilms;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info("Запрос на обновление фильма: {} ", updatedFilm);
        Film film = filmService.updateFilm(updatedFilm);
        log.info("Запрос на обновление фильма выполнен. Фильм под id {} обновлен: {} ", film.getId(), film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос от пользователя с id {} на добавление лайка к фильму с id {}", userId, id);
        filmService.addLike(id, userId);
        log.info("Запрос от пользователя с id {} на добавление лайка к фильму с id {} выполнен", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос от пользователя с id {} на удаление лайка к фильму с id {}", userId, id);
        filmService.deleteLike(id, userId);
        log.info("Запрос от пользователя с id {} на удаление лайка к фильму с id {} выполнен", userId, id);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        log.info("Запрошено на удаление фильма с id {} ", id);
        filmService.deleteFilm(id);
        log.info("Запрос на удаление фильма c id {} выполнен", id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsSortByYear(@PathVariable int directorId, @RequestParam String sortBy) {
        log.info("Запрошен список фильмов");
        List<Film> films = filmService.getFilmsSortBy(directorId, sortBy);
        log.info("Запрос на предоставление списка всех фильмов выплнен");
        return films;
    }
}
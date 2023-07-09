package ru.yandex.practicum.filmorate.controller;

import javax.validation.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.*;

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
        log.info("Запрос на создание фильма выполнен. Фильм создан: {} ", film);
        return createdFilm;
    }


    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрошен список фильмов");
        List<Film> films = filmService.getFilms();
        log.info("Запрос на предоставление списка всех фильмов выплнен");
        return films;
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
        log.info("Запрос от пользователся с id {} на добавление лайка к фильму с id {}", userId, id);
        filmService.addLike(id, userId);
        log.info("Запрос от пользователся с id {} на добавление лайка к фильму с id {} выполнен", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос от пользователся с id {} на удаление лайка к фильму с id {}", userId, id);
        filmService.deleteLike(id, userId);
        log.info("Запрос от пользователся с id {} на удаление лайка к фильму с id {} выполнен", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошен список " + count + " популярных фильмов");
        List<Film> popularFilms = filmService.getFilmsByLikes(count);
        log.info("Запрос на отправку списка " + count + " популярных фильмов выполнен");
        return popularFilms;
    }
}
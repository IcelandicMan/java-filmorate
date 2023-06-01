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
    public Film getFilm(@PathVariable long id) {
        log.info("Запрошен фильм с id: {} ", id);
        Film film = filmService.getFilmStorage().getFilm(id);
        log.info("Запрос на фильм с id {} выполнен: {} ", id, film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрошен список фильмов");
        return filmService.getFilmStorage().getFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрошен список " + count + " Популярных фильмов");
        List<Film> popularFilms = filmService.getFilmsByLikes(count);
        log.info("Запрос на отправку списка " + count + " популярных фильм выполнен");
        return popularFilms;
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("создание фильма: {} ", film);
        filmService.getFilmStorage().createFilm(film);
        log.info("Фильм добавлен: {} ", film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        log.info("Обновление фильма: {} ", updatedFilm);
        Film film = filmService.getFilmStorage().updateFilm(updatedFilm);
        log.info("Фильм под ID {} обновлен: {} ", film.getId(), film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId){
        log.info("Запрос от пользователся с id {} на добавление лайка к фильму с id {}",userId, id);
        filmService.addLike(id, userId);
        log.info("Like от пользователя с id {} к фильму {} поставлен",userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId){
        log.info("Запрос от пользователся с id {} на удаление лайка к фильму с id {}",userId, id);
        filmService.deleteLike(id, userId);
        log.info("Like от пользователя с id {} к фильму {} удален",userId, id);
    }
}
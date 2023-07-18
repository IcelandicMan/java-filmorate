package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) { // и тут
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) {
        log.info("Запрошен жанр под id: {} ", id);
        Genre genre = filmService.getGenreById(id);
        log.info("Запрос жанр под id {} выполнен: {} ", id, genre);
        return genre;
    }

    @GetMapping()
    public List<Genre> getGenres() {
        log.info("Запрошен список всех жанров");
        List<Genre> genres = filmService.getAllGenres();
        log.info("Запрос на предоставление списка всех жанров выплнен");
        return genres;
    }
}

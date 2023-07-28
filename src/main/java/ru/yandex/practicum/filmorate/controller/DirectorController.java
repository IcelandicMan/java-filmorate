package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final FilmService filmService;

    @Autowired
    public DirectorController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping()
    public List<Director> getDirectors() {
        log.info("Запрос списка всех режиссёров");
        List<Director> directors = filmService.getDirectors();
        log.info("Список режиссёров получен");
        return directors;
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") Integer id) {
        log.info("Запрошен режиссёр с id = {}", id);
        Director director = filmService.getDirectorById(id);
        log.info("Режиссёр с id = {} получен: {}", id, director);
        return director;
    }

    @PostMapping()
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на создание режиссёра: {}", director);
        Director newDirector = filmService.createDirector(director);
        log.info("Режиссёр создан: {}", director);
        return newDirector;
    }

    @PutMapping()
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на обновление режиссёра: {}", director);
        Director updateDirector = filmService.updateDirector(director);
        log.info("Режиссёр обновлён: {}", director);
        return updateDirector;
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable("id") Integer id) {
        log.info("Запрос на удаление режиссёра с id: {}", id);
        filmService.deleteDirectorById(id);
        log.info("Режиссёр удалён");
    }
}

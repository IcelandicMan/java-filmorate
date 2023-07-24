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
        return filmService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") Integer id) {
        return filmService.getDirectorById(id);
    }

    @PostMapping()
    public Director createDirector(@Valid @RequestBody Director director) {
        return filmService.createDirector(director);
    }

    @PutMapping()
    public Director updateDirector(@Valid @RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable("id") Integer id) {
        filmService.deleteDirectorById(id);
    }
}

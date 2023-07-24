package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final FilmService filmService;

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable int id) {
        log.info("Запрошен MPA Рейтинг под id: {} ", id);
        Mpa mpa = filmService.getMpaById(id);
        log.info("Запрос MPA Рейтинг под id {} выполнен: {} ", id, mpa);
        return mpa;
    }

    @GetMapping()
    public List<Mpa> getAllMpa() {
        log.info("Запрошен список всех MPA Рейтинг");
        List<Mpa> mpaList = filmService.getAllMpa();
        log.info("Запрос на предоставление списка всех MPA Рейтингов выплнен");
        return mpaList;
    }
}

package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private int idCounter = 0;

    @Override
    public Film createFilm(Film film) {
        log.info("Создание фильма: {}", film);
        film.setId(++idCounter);
        films.put(film.getId(), film);
        log.info("Фильм под id {} создан: {}", film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление фильма: {} ", updatedFilm);
        films.put(updatedFilm.getId(), updatedFilm);
        log.info("Фильм под id {} обновлен: {} ", updatedFilm.getId(), updatedFilm);
        return updatedFilm;
    }

    @Override
    public void deleteFilm(long id) {
        log.info("Удаление фильма с id {}", id);
        films.remove(id);
        log.info("Фильм с id {} удален", id);
    }

    @Override
    public Film getFilm(long id) {
        log.info("Получение Фильма с id {}", id);
        Film film = films.get(id);
        if (film == null) {
            log.warn("Фильм под id {} не найден", id);
            throw new FilmNotFoundException(String.format("Фильм c id %s не найден", id));
        }
        log.info("Фильма с id {} получен: {}", id, film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов");
        List<Film> filmsList = new ArrayList<>(films.values());
        log.info("Список всех фильмов получен");
        return filmsList;
    }
}

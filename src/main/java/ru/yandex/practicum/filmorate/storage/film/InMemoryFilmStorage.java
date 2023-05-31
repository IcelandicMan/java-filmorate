package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> users = new HashMap<>();
    private int idCounter = 0;
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public void deleteFilm(long id) {

    }

    @Override
    public Film getFilm(long id) {
        return null;
    }

    @Override
    public List<Film> getFilms(int id) {
        return null;
    }
}

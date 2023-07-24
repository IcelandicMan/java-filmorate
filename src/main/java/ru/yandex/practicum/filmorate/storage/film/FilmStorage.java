package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id);

    Film getFilm(int id);

    List<Film> getFilms();

    List<Film> getFilmsSortByYear(Integer id);

    List<Film> getFilmsSortByLikes(Integer id);
}


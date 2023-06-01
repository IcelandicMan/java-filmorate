package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    //Делаю ровно по ТЗ, "в которых будут определены методы добавления, удаления и модификации объектов"
    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(long id);
}


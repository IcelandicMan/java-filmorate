package ru.yandex.practicum.filmorate.storage.film.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    void deleteGenresByFilmId(int id);
}

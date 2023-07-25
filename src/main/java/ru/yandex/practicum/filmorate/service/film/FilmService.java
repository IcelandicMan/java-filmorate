package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;


    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaStorage mpaStorage,
            GenreStorage genreStorage,
            LikeStorage likeStorage,
            DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
        this.directorStorage = directorStorage;
    }

    public Film createFilm(Film film) {
        Film crearedFilm = filmStorage.createFilm(film);
        return getFilm(crearedFilm.getId());
    }

    public Film updateFilm(Film updatedFilm) {
        filmStorage.updateFilm(updatedFilm);
        return getFilm(updatedFilm.getId());
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    public Film getFilm(int id) {
        final Film film = filmStorage.getFilm(id);
        genreStorage.load(Collections.singletonList(film));
        directorStorage.load(Collections.singletonList(film));
        return film;
    }

    public List<Film> getFilms() {
        final List<Film> films = filmStorage.getFilms();
        genreStorage.load(films);
        directorStorage.load(films);
        return films;
    }

    public void addLike(int filmId, int userId) {
        User user = userStorage.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        User user = userStorage.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        likeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getFilmsByLikes(int count) {
        return likeStorage.getFilmsByLikes(count);
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public void deleteDirectorById(Integer id) {
        directorStorage.deleteDirectorById(id);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public List<Film> getFilmsSortBy(Integer id, String sortBy) {
        if (sortBy.equals("year")) {
            sortBy = "RELEASEDATE";
        } else if (sortBy.equals("likes")) {
            sortBy += " DESC";
        }
        return Collections.unmodifiableList(filmStorage.getFilmsSortBy(id, sortBy));
    }
}
package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.recommendation.RecommendationDbStorage;
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
    private final RecommendationDbStorage recommendationDbStorage;


    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaStorage mpaStorage,
            GenreStorage genreStorage,
            LikeStorage likeStorage,
            RecommendationDbStorage recommendationDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
        this.recommendationDbStorage = recommendationDbStorage;
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
        return film;
    }

    public List<Film> getFilms() {
        final List<Film> films = filmStorage.getFilms();
        genreStorage.load(films);
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

    public List<Film> getRecommendation(int userId) {
        List<Film> films = recommendationDbStorage.getRecommendation(userId);
        genreStorage.load(films);
        // В дальнейшем нужно добавить режисеров load
        return films;
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
}
package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LikeFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaStorage mpaStorage,
            GenreStorage genreStorage,
            JdbcTemplate jdbcTemplate) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.jdbcTemplate = jdbcTemplate;
    }
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film updatedFilm) {
        filmStorage.getFilm(updatedFilm.getId());
        return filmStorage.updateFilm(updatedFilm);
    }

    public void deleteFilm(int id) {
        filmStorage.getFilm(id);
        filmStorage.deleteFilm(id);
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }


    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка от пользователя с id {} к фильму с id {}", userId, filmId);
        userStorage.getUser(userId);
        isValidLikes(filmId, userId);
        Film film = filmStorage.getFilm(filmId);

        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?,?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Пользователь с id {} поставил лайк к фильму с id {}", userId, filmId);

        String updateRate = "UPDATE films SET film_rate = film_rate+1 WHERE film_id = ?";
        jdbcTemplate.update(updateRate, filmId);
        log.info("Рейтинг фильма с id {} изменен", filmId);
    }

    private void isValidLikes(int filmId, int userId) {
        String sql = "SELECT COUNT(*) AS count FROM film_likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, userId);
        if (count != null && count > 0) {
            log.warn("Пользователь с id {} уже ставил лайк фильму с id {}", userId, filmId);
            throw new LikeFoundException("Пользователь уже ставил лайк данному фильму");
        }
    }

    public void deleteLike(int filmId, int userId) {
        log.info("Удаление лайка от пользователя с id {} к фильму с id {}", userId, filmId);
        userStorage.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        String sql = "DELETE FROM film_likes WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
        log.info("Пользователь с id {} удалил лайк к фильму с id {}", userId, filmId);

        String updateRate = "UPDATE films SET film_rate = film_rate-1 WHERE film_id = ?";
        jdbcTemplate.update(updateRate, filmId);
        log.info("Рейтинг фильма с id {} изминен", filmId);
    }

    public List<Film> getFilmsByLikes(int count) {
        log.info("Получение списка " + count + " лучших фильмов по количеству лайков");
        String sql = "Select film_id FROM films ORDER BY film_rate DESC LIMIT ?";
        List<Integer> filmsId = jdbcTemplate.query(sql, new Object[]{count}, (rs, rowNum) -> rs.getInt("film_id"));
        List<Film> filmsList = new ArrayList<>();
        for (Integer id : filmsId) {
            Film film = filmStorage.getFilm(id);
            filmsList.add(film);
        }
        log.info("Список " + count + " лучших фильмов по количеству лайков предоставлен");
        return filmsList;
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
/*
 if (mpaId != 0) {
         Mpa mpa = mpaStorage.getMpaById(mpaId);
         film.setMpa(mpa);
         }

    createdFilm.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));




               private boolean isGenreExistsForFilm(int filmId, int genreId) {
        String sql = "SELECT COUNT(*) FROM film_genres WHERE film_id = ? AND genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId, genreId);
        return count != null && count > 0;
    }
 */
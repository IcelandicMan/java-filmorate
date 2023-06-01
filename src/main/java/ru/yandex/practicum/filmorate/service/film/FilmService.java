package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LikeFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;


import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        log.info("Запрос от пользователя с id {}, на добавление лайка к фильму с id {}", userId, filmId);
        userStorage.isValidUserId(userId);
        isValidLikes(filmId, userId);
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().add(userId);
        log.info("Пользователь с id {} поставил лайк к фильму с id {}", userId, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        log.info("Запрос от пользователя с id {}, на удаление лайка к фильму с id {}", userId, filmId);
        userStorage.isValidUserId(userId);
        isValidLikes(filmId, userId);
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().remove(userId);
        log.info("Пользователь с id {} удалил лайк к фильму с id {}", userId, filmId);
    }

    public List<Film> getFilmsByLikes(int count) {
        log.info("Запрос на получение " + count + " лучших фильмов по количеству лайков");
        List<Film> filmList = filmStorage.getFilms();
        Comparator<Film> likesComparator = Comparator.comparingLong(film -> film.getLikes().size());
        filmList.sort(likesComparator.reversed());

        int limit = Math.min(filmList.size(), count);
        return filmList.subList(0, limit);
    }

    private void isValidLikes(long filmId, long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь с id {} уже ставил лайк фильму с id {}", userId, filmId);
            throw new LikeFoundException("Пользователь уже ставил лайк данному фильму");
        }
    }

    public InMemoryFilmStorage getFilmStorage() {
        return filmStorage;
    }
}

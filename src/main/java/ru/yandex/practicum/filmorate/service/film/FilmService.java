package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.LikeFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film updatedFilm) {
        filmStorage.getFilm(updatedFilm.getId());
        return filmStorage.updateFilm(updatedFilm);
    }

    public void deleteFilm(long id) {
        filmStorage.getFilm(id);
        filmStorage.deleteFilm(id);
    }

    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void addLike(long filmId, long userId) {
        log.info("Добавление лайка от пользователя с id {} к фильму с id {}", userId, filmId);
        userStorage.getUser(userId);
        isValidLikes(filmId, userId);
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().add(userId);
        log.info("Пользователь с id {} поставил лайк к фильму с id {}", userId, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        log.info("Удаление лайка от пользователя с id {} к фильму с id {}", userId, filmId);
        userStorage.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().remove(userId);
        log.info("Пользователь с id {} удалил лайк к фильму с id {}", userId, filmId);
    }

    public List<Film> getFilmsByLikes(int count) {
        log.info("Получение списка " + count + " лучших фильмов по количеству лайков");
        List<Film> filmList = filmStorage.getFilms();
        Comparator<Film> likesComparator = Comparator.comparingLong(film -> film.getLikes().size());
        filmList.sort(likesComparator.reversed());

        int limit = Math.min(filmList.size(), count);
        log.info("Список " + count + " лучших фильмов по количеству лайков предоставлен");
        return filmList.subList(0, limit);
    }

    private void isValidLikes(long filmId, long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь с id {} уже ставил лайк фильму с id {}", userId, filmId);
            throw new LikeFoundException("Пользователь уже ставил лайк данному фильму");
        }
    }
}

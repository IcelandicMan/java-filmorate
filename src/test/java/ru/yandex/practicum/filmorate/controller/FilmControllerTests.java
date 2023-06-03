package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTests {

    private FilmController filmController;
    private InMemoryUserStorage userStorage;

    @BeforeEach
    public void setup() {
        userStorage = new InMemoryUserStorage();
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
    }

    @Test
    @DisplayName("1. Создаем фильм и получаем фильм" + "\n" +
            "2. Обновляем фильм и получаем фильм" + "\n" +
            "3. Создаем второй фильм" + "\n" +
            "4. Получаем список фильмов" + "\n" +
            "5. Создаем Пользователя" + "\n" +
            "6. Добавляем лайк фильму от пользователя" + "\n" +
            "7. Получаем список лучших фильмов" + "\n" +
            "8. Удаляем лайк и получаем список лучших фильмов"
    )

    public void createFilmsAndAddLikes() {
        Film film = new Film();
        film.setName("Тестовый фильм");

        Film createdFilm = filmController.createFilm(film);
        long filmId = createdFilm.getId();

        assertEquals(createdFilm, filmController.getFilm(filmId));

        Film updatedFilm = new Film();
        updatedFilm.setId(filmId);
        updatedFilm.setName("Обновленный тестовый фильм");

        Film upFilm = filmController.updateFilm(updatedFilm);
        assertEquals(upFilm, filmController.getFilm(filmId));

        Film secondFilm = new Film();
        secondFilm.setName("Второй фильм");
        Film createdSecondFilm = filmController.createFilm(secondFilm);
        long secondFilmId = createdSecondFilm.getId();

        assertEquals(2, filmController.getAllFilms().size());
        assertEquals(upFilm, filmController.getAllFilms().get(0));
        assertEquals(createdSecondFilm, filmController.getAllFilms().get(1));

        User user = new User();
        user.setName("Пользователь");
        long userId = userStorage.createUser(user).getId();

        filmController.addLike(filmId, userId);

        assertEquals(1, filmController.getFilm(filmId).getLikes().size());
        assertTrue(filmController.getFilm(filmId).getLikes().contains(userId));

        assertEquals(1, filmController.getPopularFilms(1).size());
        assertEquals(upFilm, filmController.getPopularFilms(1).get(0));

        filmController.deleteLike(filmId, userId);
        assertEquals(0, filmController.getFilm(filmId).getLikes().size());
    }
}
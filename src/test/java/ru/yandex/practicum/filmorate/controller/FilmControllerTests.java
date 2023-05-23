package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests {

    private FilmController filmController;

    @BeforeEach
    public void setup() {
        filmController = new FilmController();
    }

    @Test
    public void createEmptyFilm_ShouldThrowValidationException() {
        Film film = new Film();

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void createFilm_WithEmptyName_ShouldThrowValidationException() {
        Film film = new Film();
        film.setDescription("Test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void createFilm_WithMoreThenMaxSizeDescription_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Heima");
        film.setDescription(new String(new char[201]));
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void createFilm_WithMinusDuration_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Heima");
        film.setDescription("Test");
        film.setDuration(-100);
        film.setReleaseDate(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }


    @Test
    public void createFilm_WithWrongData_ShouldThrowValidationException() {
        Film film = new Film();
        film.setName("Heima");
        film.setDescription("Test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1890, 1, 1));

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    public void createFilm_ShouldReturnCreatedFilm() {
        Film film = new Film();
        film.setName("Heima");
        film.setDescription("Test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1900, 1, 1));

        Film createdFilm = filmController.createFilm(film);

        assertNotNull(createdFilm.getId());
        assertEquals("Heima", createdFilm.getName());
        assertEquals("Test", createdFilm.getDescription());
        assertEquals(100, createdFilm.getDuration());
        assertEquals(LocalDate.of(1900, 1, 1), createdFilm.getReleaseDate());
    }
}